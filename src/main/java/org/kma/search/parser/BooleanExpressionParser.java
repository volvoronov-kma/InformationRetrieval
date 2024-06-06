package org.kma.search.parser;

import org.kma.index.domain.NGramOrder;
import org.kma.search.query.*;

import java.util.*;

public class BooleanExpressionParser {

    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String NOT = "NOT";
    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList(AND, OR, NOT));

    private StringTokenizer tokenizer;
    private String currentToken;
    private NGramOrder nGramOrder = NGramOrder.UNIGRAM;
    private ParserType parserType;

    public enum ParserType {
        POSITIONAL,
        NGRAM
    }

    public BooleanExpressionParser(ParserType type) {
        parserType = type;
    }

    public NGramOrder getnGramOrder() {
        return nGramOrder;
    }

    public void setnGramOrder(NGramOrder nGramOrder) {
        this.nGramOrder = nGramOrder;
    }

    public BooleanQuery parse(String expression) {
        tokenizer = new StringTokenizer(expression, " ()\"", true);
        currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        return parseExpression();
    }

    public BooleanQuery parse(String expression, NGramOrder nGramOrder) {
        this.nGramOrder = nGramOrder;
        tokenizer = new StringTokenizer(expression, " ()\"", true);
        currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        return parseExpression();
    }

    private BooleanQuery parseExpression() {
        BooleanQuery result = parseTerm();
        while (currentToken != null && (currentToken.equals(AND) || currentToken.equals(OR))) {
            String operator = currentToken;
            nextToken();
            BooleanQuery right = parseTerm();
            if (operator.equals(AND)) {
                result = new AndQuery(result, right);
            } else if (operator.equals(OR)) {
                result = new OrQuery(result, right);
            }
        }
        return result;
    }

    private BooleanQuery parseTerm() {
        if (currentToken == null) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        if (currentToken.equals("(")) {
            nextToken();
            BooleanQuery result = parseExpression();
            if (!currentToken.equals(")")) {
                throw new IllegalArgumentException("Expected closing parenthesis");
            }
            nextToken();
            return result;
        } else if (currentToken.equals(NOT)) {
            nextToken();
            return new NotQuery(parseTerm());
        } else if (!OPERATORS.contains(currentToken)) {
            BooleanQuery term;
            if (currentToken.equals("\"")) {
                if (this.parserType.equals(ParserType.POSITIONAL)) {
                    term = parseQuotedPhraseToPhraseQuery();
                } else {
                    term = parseQuotedPhraseToNgrams();
                }
            } else {
                term = new TermQuery(currentToken);
            }
            nextToken();
            return term;
        } else {
            throw new IllegalArgumentException("Unexpected token: " + currentToken);
        }
    }

    private BooleanQuery parseQuotedPhraseToPhraseQuery() {
        StringBuilder phrase = new StringBuilder();
        nextToken();
        while (currentToken != null && !currentToken.equals("\"")) {
            if (phrase.length() > 0) {
                phrase.append(" ");
            }
            phrase.append(currentToken);
            nextToken();
        }
        if (currentToken == null) {
            throw new IllegalArgumentException("Unterminated quoted phrase");
        }
        String[] words = phrase.toString().split(" ");
        BooleanQuery result = new PhraseQuery(List.of(words));
        nextToken();
        return result;
    }

    private BooleanQuery parseQuotedPhraseToNgrams() {
        StringBuilder phrase = new StringBuilder();
        nextToken();
        while (currentToken != null && !currentToken.equals("\"")) {
            if (phrase.length() > 0) {
                phrase.append(" ");
            }
            phrase.append(currentToken);
            nextToken();
        }
        if (currentToken == null) {
            throw new IllegalArgumentException("Unterminated quoted phrase");
        }
        String[] words = phrase.toString().split(" ");
        BooleanQuery result;
        if (this.nGramOrder == NGramOrder.BIGRAM) {
            if (words.length < 2) {
                throw new IllegalArgumentException("Quoted phrase must contain at least two words for bigrams");
            }

            result = new TermQuery(words[0] + " " + words[1]);
            for (int i = 1; i < words.length - 1; i++) {
                BooleanQuery nextBigram = new TermQuery(words[i] + " " + words[i + 1]);
                result = new AndQuery(result, nextBigram);
            }
        } else {
            if (words.length < 1) {
                throw new IllegalArgumentException("Quoted phrase must contain at least one word for unigram");
            }

            result = new TermQuery(words[0]);
            for (int i = 1; i < words.length; i++) {
                BooleanQuery nextUnigram = new TermQuery(words[i]);
                result = new AndQuery(result, nextUnigram);
            }
        }
        nextToken();
        return result;
    }

    private void nextToken() {
        currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        while (currentToken != null && currentToken.trim().isEmpty()) {
            currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        }
    }
}
