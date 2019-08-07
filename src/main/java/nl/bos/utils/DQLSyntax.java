package nl.bos.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import com.amplexor.DQL;
import com.amplexor.DQLLexer;

public class DQLSyntax {
	private static final Logger LOGGER = Logger.getLogger(DQLSyntax.class.getName());
	
	private static final String[] KEYWORDS = new String[] { "ABORT", "ALTER", "ASSEMBLIES", "ACL", "AND", "ASSEMBLY",
			"ADD", "ANY", "ASSISTANCE", "ADD_FTINDEX", "APPEND", "ATTR", "ADDRESS", "APPLICATION", "AUTO", "ALL", "AS",
			"AVG", "ALLOW", "ASC", "BAG", "BOOL", "BUSINESS", "BEGIN", "BOOLEAN", "BY", "BETWEEN", "BROWSE", "CABINET",
			"COMMENT", "CONTAINS", "CACHING", "COMMIT", "CONTENT_FORMAT", "CHANGE", "COMPLETE", "CONTENT_ID",
			"CHARACTER", "COMPONENTS", "COUNT", "CHARACTERS", "COMPOSITE", "CREATE", "CHAR", "COMPUTED", "CURRENT",
			"CHECK", "CONTAIN_ID", "DATE", "DELETED", "DISTINCT", "DATEADD", "DEPENDENCY", "DM_SESSION_DD_LOCALE",
			"DATEDIFF", "DEPTH", "DOCBASIC", "DATEFLOOR", "DESC", "DOCUMENT", "DATETOSTRING", "DESCEND", "DOUBLE",
			"DAY", "DISABLE", "DROP", "DEFAULT", "DISALLOW", "DROP_FTINDEX", "DELETE", "DISPLAY", "ELSE", "ENFORCE",
			"EXEC", "ELSEIF", "ESCAPE", "EXECUTE", "ENABLE", "ESTIMATE", "EXISTS", "FALSE", "FOR", "FT_OPTIMIZER",
			"FIRST", "FOREIGN", "FULLTEXT", "FLOAT", "FROM", "FUNCTION", "FOLDER", "FTINDEX", "GRANT", "GROUP",
			"HAVING", "HITS", "ID", "INT", "IS", "IF", "INTEGER", "ISCURRENT", "IN", "INTERNAL", "ISPUBLIC", "INSERT",
			"INTO", "ISREPLICA", "JOIN", "KEY", "LANGUAGE", "LIGHTWEIGHT", "LITE", "LAST", "LIKE", "LOWER", "LATEST",
			"LINK", "LEFT", "LIST", "MAPPING", "MEMBERS", "MONTH", "MATERIALIZE", "MFILE_URL", "MOVE",
			"MATERIALIZATION", "MHITS", "MSCORE", "MAX", "MIN", "MCONTENTID", "MODIFY", "NODE", "NOTE", "NULLID",
			"NODESORT", "NOW", "NULLINT", "NONE", "NULL", "NULLSTRING", "NOT", "NULLDATE", "OF", "ON", "ORDER",
			"OBJECT", "ONLY", "OUTER", "OBJECTS", "OR", "OWNER", "PAGE_NO", "PERMIT", "PRIVATE", "PARENT", "POLICY",
			"PRIVILEGES", "PARTITION", "POSITION", "PROPERTY", "PATH", "PRIMARY", "PUBLIC", "QRY", "QUALIFIABLE",
			"RDBMS", "RELATE", "REPORT", "READ", "REMOVE", "REQUEST", "REFERENCES", "REPEATING", "REVOKE", "REGISTER",
			"REPLACEIF", "SCORE", "SMALLINT", "SUPERTYPE", "SEARCH", "SOME", "SUPERUSER", "SELECT", "STATE", "SUPPORT",
			"SEPARATOR", "STORAGE", "SYNONYM", "SERVER", "STRING", "SYSADMIN", "SET", "SUBSTR", "SYSOBJ_ID", "SETFILE",
			"SUBSTRING", "SYSTEM", "SHAREABLE", "SUM", "SHARES", "SUMMARY", "TABLE", "TO", "TRANSACTION", "TAG",
			"TODAY", "TRUE", "TEXT", "TOMORROW", "TRUNCATE", "TIME", "TOPIC", "TYPE", "TINYINT", "TRAN", "UNION",
			"UNREGISTER", "UPDATE", "UNIQUE", "USER", "UPPER", "UNLINK", "USING", "VALUE", "VERSION", "VIOLATION",
			"VALUES", "VERITY", "WHERE", "WITHOUT", "WEEK", "WITH", "WORLD", "WITHIN", "WRITE", "YEAR", "YESTERDAY" };

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\'([^\'\\\\]|\\\\.)*\'";

	private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>"
			+ PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
			+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")",
			Pattern.CASE_INSENSITIVE);

	public static StyleSpans<Collection<String>> computeHighlighting(String text) {
		DQLLexer lexer = new DQLLexer(CharStreams.fromString(text));
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		for (Token tok : lexer.getAllTokens()) {
			String styleClass = "";
			if (Arrays.stream(DQL.keywords).anyMatch(id -> id == tok.getType())) {
				LOGGER.info("Keywords");
				styleClass = "keyword";
			} else if (DQLLexer.OPEN_PAR==tok.getType()|DQLLexer.CLOSE_PAR==tok.getType()) {
				LOGGER.info("paren");
				styleClass = "paren";
			} else if (DQLLexer.STRING_LITERAL==tok.getType()) {
				styleClass = "string";
			}
			LOGGER.info("Start " + tok.getStartIndex() + " end " + tok.getStopIndex() + " last end " + lastKwEnd);
			if (tok.getStartIndex() - lastKwEnd > 0) {
				spansBuilder.add(Collections.emptyList(), tok.getStartIndex() - lastKwEnd);
			}
			spansBuilder.add(Collections.singleton(styleClass), tok.getText().length());
			lastKwEnd = tok.getStopIndex() + 1;
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}

	public static StyleSpans<Collection<String>> computeHighlightingREGEX(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("KEYWORD") != null ? "keyword"
					: matcher.group("PAREN") != null ? "paren"
							: matcher.group("BRACE") != null ? "brace"
									: matcher.group("BRACKET") != null ? "bracket"
											: matcher.group("SEMICOLON") != null ? "semicolon"
													: matcher.group("STRING") != null ? "string" : null;
			/* never happens */ assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
