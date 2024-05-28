package org.kma.search.parser;

import org.kma.search.query.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BooleanExpressionParser {

    private static final String AND = "AND";
    private static final String OR = "OR";
    private static final String NOT = "NOT";
    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList(AND, OR, NOT));

    private StringTokenizer tokenizer;
    private String currentToken;

    public BooleanQuery parse(String expression) {
        tokenizer = new StringTokenizer(expression, " ()", true);
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
            BooleanQuery term = new TermQuery(currentToken);
            nextToken();
            return term;
        } else {
            throw new IllegalArgumentException("Unexpected token: " + currentToken);
        }
    }

    private void nextToken() {
        currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        while (currentToken != null && currentToken.trim().isEmpty()) {
            currentToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        }
    }
}
