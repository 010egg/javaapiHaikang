// 
// Decompiled by Procyon v0.5.36
// 

package com.typesafe.config.impl;

import java.util.HashMap;
import com.typesafe.config.ConfigMergeable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.ListIterator;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.typesafe.config.ConfigException;
import java.util.LinkedList;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigOrigin;

final class ConfigParser
{
    static AbstractConfigValue parse(final ConfigNodeRoot document, final ConfigOrigin origin, final ConfigParseOptions options, final ConfigIncludeContext includeContext) {
        final ParseContext context = new ParseContext(options.getSyntax(), origin, document, SimpleIncluder.makeFull(options.getIncluder()), includeContext);
        return context.parse();
    }
    
    private static final class ParseContext
    {
        private int lineNumber;
        private final ConfigNodeRoot document;
        private final FullIncluder includer;
        private final ConfigIncludeContext includeContext;
        private final ConfigSyntax flavor;
        private final ConfigOrigin baseOrigin;
        private final LinkedList<Path> pathStack;
        int arrayCount;
        
        ParseContext(final ConfigSyntax flavor, final ConfigOrigin origin, final ConfigNodeRoot document, final FullIncluder includer, final ConfigIncludeContext includeContext) {
            this.lineNumber = 1;
            this.document = document;
            this.flavor = flavor;
            this.baseOrigin = origin;
            this.includer = includer;
            this.includeContext = includeContext;
            this.pathStack = new LinkedList<Path>();
            this.arrayCount = 0;
        }
        
        private AbstractConfigValue parseConcatenation(final ConfigNodeConcatenation n) {
            if (this.flavor == ConfigSyntax.JSON) {
                throw new ConfigException.BugOrBroken("Found a concatenation node in JSON");
            }
            final List<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>();
            for (final AbstractConfigNode node : n.children()) {
                AbstractConfigValue v = null;
                if (node instanceof AbstractConfigNodeValue) {
                    v = this.parseValue((AbstractConfigNodeValue)node, null);
                    values.add(v);
                }
            }
            return ConfigConcatenation.concatenate(values);
        }
        
        private SimpleConfigOrigin lineOrigin() {
            return ((SimpleConfigOrigin)this.baseOrigin).withLineNumber(this.lineNumber);
        }
        
        private ConfigException parseError(final String message) {
            return this.parseError(message, null);
        }
        
        private ConfigException parseError(final String message, final Throwable cause) {
            return new ConfigException.Parse(this.lineOrigin(), message, cause);
        }
        
        private Path fullCurrentPath() {
            if (this.pathStack.isEmpty()) {
                throw new ConfigException.BugOrBroken("Bug in parser; tried to get current path when at root");
            }
            return new Path(this.pathStack.descendingIterator());
        }
        
        private AbstractConfigValue parseValue(final AbstractConfigNodeValue n, final List<String> comments) {
            final int startingArrayCount = this.arrayCount;
            AbstractConfigValue v;
            if (n instanceof ConfigNodeSimpleValue) {
                v = ((ConfigNodeSimpleValue)n).value();
            }
            else if (n instanceof ConfigNodeObject) {
                v = this.parseObject((ConfigNodeObject)n);
            }
            else if (n instanceof ConfigNodeArray) {
                v = this.parseArray((ConfigNodeArray)n);
            }
            else {
                if (!(n instanceof ConfigNodeConcatenation)) {
                    throw this.parseError("Expecting a value but got wrong node type: " + n.getClass());
                }
                v = this.parseConcatenation((ConfigNodeConcatenation)n);
            }
            if (comments != null && !comments.isEmpty()) {
                v = v.withOrigin((ConfigOrigin)v.origin().prependComments(new ArrayList<String>(comments)));
                comments.clear();
            }
            if (this.arrayCount != startingArrayCount) {
                throw new ConfigException.BugOrBroken("Bug in config parser: unbalanced array count");
            }
            return v;
        }
        
        private static AbstractConfigObject createValueUnderPath(final Path path, final AbstractConfigValue value) {
            final List<String> keys = new ArrayList<String>();
            String key = path.first();
            for (Path remaining = path.remainder(); key != null; key = remaining.first(), remaining = remaining.remainder()) {
                keys.add(key);
                if (remaining == null) {
                    break;
                }
            }
            final ListIterator<String> i = keys.listIterator(keys.size());
            final String deepest = i.previous();
            AbstractConfigObject o = new SimpleConfigObject(value.origin().withComments((List<String>)null), Collections.singletonMap(deepest, value));
            while (i.hasPrevious()) {
                final Map<String, AbstractConfigValue> m = (Map<String, AbstractConfigValue>)Collections.singletonMap(i.previous(), o);
                o = new SimpleConfigObject(value.origin().withComments((List<String>)null), m);
            }
            return o;
        }
        
        private void parseInclude(final Map<String, AbstractConfigValue> values, final ConfigNodeInclude n) {
            final boolean isRequired = n.isRequired();
            final ConfigIncludeContext cic = this.includeContext.setParseOptions(this.includeContext.parseOptions().setAllowMissing(!isRequired));
            AbstractConfigObject obj = null;
            switch (n.kind()) {
                case URL: {
                    URL url;
                    try {
                        url = new URL(n.name());
                    }
                    catch (MalformedURLException e) {
                        throw this.parseError("include url() specifies an invalid URL: " + n.name(), e);
                    }
                    obj = (AbstractConfigObject)this.includer.includeURL(cic, url);
                    break;
                }
                case FILE: {
                    obj = (AbstractConfigObject)this.includer.includeFile(cic, new File(n.name()));
                    break;
                }
                case CLASSPATH: {
                    obj = (AbstractConfigObject)this.includer.includeResources(cic, n.name());
                    break;
                }
                case HEURISTIC: {
                    obj = (AbstractConfigObject)this.includer.include(cic, n.name());
                    break;
                }
                default: {
                    throw new ConfigException.BugOrBroken("should not be reached");
                }
            }
            if (this.arrayCount > 0 && obj.resolveStatus() != ResolveStatus.RESOLVED) {
                throw this.parseError("Due to current limitations of the config parser, when an include statement is nested inside a list value, ${} substitutions inside the included file cannot be resolved correctly. Either move the include outside of the list value or remove the ${} statements from the included file.");
            }
            if (!this.pathStack.isEmpty()) {
                final Path prefix = this.fullCurrentPath();
                obj = obj.relativized(prefix);
            }
            for (final String key : ((Map<String, V>)obj).keySet()) {
                final AbstractConfigValue v = obj.get(key);
                final AbstractConfigValue existing = values.get(key);
                if (existing != null) {
                    values.put(key, v.withFallback((ConfigMergeable)existing));
                }
                else {
                    values.put(key, v);
                }
            }
        }
        
        private AbstractConfigObject parseObject(final ConfigNodeObject n) {
            final Map<String, AbstractConfigValue> values = new HashMap<String, AbstractConfigValue>();
            final SimpleConfigOrigin objectOrigin = this.lineOrigin();
            boolean lastWasNewline = false;
            final ArrayList<AbstractConfigNode> nodes = new ArrayList<AbstractConfigNode>(n.children());
            final List<String> comments = new ArrayList<String>();
            for (int i = 0; i < nodes.size(); ++i) {
                final AbstractConfigNode node = nodes.get(i);
                if (node instanceof ConfigNodeComment) {
                    lastWasNewline = false;
                    comments.add(((ConfigNodeComment)node).commentText());
                }
                else if (node instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)node).token())) {
                    ++this.lineNumber;
                    if (lastWasNewline) {
                        comments.clear();
                    }
                    lastWasNewline = true;
                }
                else if (this.flavor != ConfigSyntax.JSON && node instanceof ConfigNodeInclude) {
                    this.parseInclude(values, (ConfigNodeInclude)node);
                    lastWasNewline = false;
                }
                else if (node instanceof ConfigNodeField) {
                    lastWasNewline = false;
                    final Path path = ((ConfigNodeField)node).path().value();
                    comments.addAll(((ConfigNodeField)node).comments());
                    this.pathStack.push(path);
                    if (((ConfigNodeField)node).separator() == Tokens.PLUS_EQUALS) {
                        if (this.arrayCount > 0) {
                            throw this.parseError("Due to current limitations of the config parser, += does not work nested inside a list. += expands to a ${} substitution and the path in ${} cannot currently refer to list elements. You might be able to move the += outside of the list and then refer to it from inside the list with ${}.");
                        }
                        ++this.arrayCount;
                    }
                    final AbstractConfigNodeValue valueNode = ((ConfigNodeField)node).value();
                    AbstractConfigValue newValue = this.parseValue(valueNode, comments);
                    if (((ConfigNodeField)node).separator() == Tokens.PLUS_EQUALS) {
                        --this.arrayCount;
                        final List<AbstractConfigValue> concat = new ArrayList<AbstractConfigValue>(2);
                        final AbstractConfigValue previousRef = new ConfigReference(newValue.origin(), new SubstitutionExpression(this.fullCurrentPath(), true));
                        final AbstractConfigValue list = new SimpleConfigList(newValue.origin(), Collections.singletonList(newValue));
                        concat.add(previousRef);
                        concat.add(list);
                        newValue = ConfigConcatenation.concatenate(concat);
                    }
                    if (i < nodes.size() - 1) {
                        ++i;
                        while (i < nodes.size()) {
                            if (nodes.get(i) instanceof ConfigNodeComment) {
                                final ConfigNodeComment comment = nodes.get(i);
                                newValue = newValue.withOrigin((ConfigOrigin)newValue.origin().appendComments(Collections.singletonList(comment.commentText())));
                                break;
                            }
                            if (!(nodes.get(i) instanceof ConfigNodeSingleToken)) {
                                --i;
                                break;
                            }
                            final ConfigNodeSingleToken curr = nodes.get(i);
                            if (curr.token() != Tokens.COMMA && !Tokens.isIgnoredWhitespace(curr.token())) {
                                --i;
                                break;
                            }
                            ++i;
                        }
                    }
                    this.pathStack.pop();
                    final String key = path.first();
                    final Path remaining = path.remainder();
                    if (remaining == null) {
                        final AbstractConfigValue existing = values.get(key);
                        if (existing != null) {
                            if (this.flavor == ConfigSyntax.JSON) {
                                throw this.parseError("JSON does not allow duplicate fields: '" + key + "' was already seen at " + existing.origin().description());
                            }
                            newValue = newValue.withFallback((ConfigMergeable)existing);
                        }
                        values.put(key, newValue);
                    }
                    else {
                        if (this.flavor == ConfigSyntax.JSON) {
                            throw new ConfigException.BugOrBroken("somehow got multi-element path in JSON mode");
                        }
                        AbstractConfigObject obj = createValueUnderPath(remaining, newValue);
                        final AbstractConfigValue existing2 = values.get(key);
                        if (existing2 != null) {
                            obj = obj.withFallback(existing2);
                        }
                        values.put(key, obj);
                    }
                }
            }
            return new SimpleConfigObject(objectOrigin, values);
        }
        
        private SimpleConfigList parseArray(final ConfigNodeArray n) {
            ++this.arrayCount;
            final SimpleConfigOrigin arrayOrigin = this.lineOrigin();
            final List<AbstractConfigValue> values = new ArrayList<AbstractConfigValue>();
            boolean lastWasNewLine = false;
            final List<String> comments = new ArrayList<String>();
            AbstractConfigValue v = null;
            for (final AbstractConfigNode node : n.children()) {
                if (node instanceof ConfigNodeComment) {
                    comments.add(((ConfigNodeComment)node).commentText());
                    lastWasNewLine = false;
                }
                else if (node instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)node).token())) {
                    ++this.lineNumber;
                    if (lastWasNewLine && v == null) {
                        comments.clear();
                    }
                    else if (v != null) {
                        values.add(v.withOrigin((ConfigOrigin)v.origin().appendComments(new ArrayList<String>(comments))));
                        comments.clear();
                        v = null;
                    }
                    lastWasNewLine = true;
                }
                else {
                    if (!(node instanceof AbstractConfigNodeValue)) {
                        continue;
                    }
                    lastWasNewLine = false;
                    if (v != null) {
                        values.add(v.withOrigin((ConfigOrigin)v.origin().appendComments(new ArrayList<String>(comments))));
                        comments.clear();
                    }
                    v = this.parseValue((AbstractConfigNodeValue)node, comments);
                }
            }
            if (v != null) {
                values.add(v.withOrigin((ConfigOrigin)v.origin().appendComments(new ArrayList<String>(comments))));
            }
            --this.arrayCount;
            return new SimpleConfigList(arrayOrigin, values);
        }
        
        AbstractConfigValue parse() {
            AbstractConfigValue result = null;
            final ArrayList<String> comments = new ArrayList<String>();
            boolean lastWasNewLine = false;
            for (final AbstractConfigNode node : this.document.children()) {
                if (node instanceof ConfigNodeComment) {
                    comments.add(((ConfigNodeComment)node).commentText());
                    lastWasNewLine = false;
                }
                else if (node instanceof ConfigNodeSingleToken) {
                    final Token t = ((ConfigNodeSingleToken)node).token();
                    if (!Tokens.isNewline(t)) {
                        continue;
                    }
                    ++this.lineNumber;
                    if (lastWasNewLine && result == null) {
                        comments.clear();
                    }
                    else if (result != null) {
                        result = result.withOrigin((ConfigOrigin)result.origin().appendComments(new ArrayList<String>(comments)));
                        comments.clear();
                        break;
                    }
                    lastWasNewLine = true;
                }
                else {
                    if (!(node instanceof ConfigNodeComplexValue)) {
                        continue;
                    }
                    result = this.parseValue((AbstractConfigNodeValue)node, comments);
                    lastWasNewLine = false;
                }
            }
            return result;
        }
    }
}
