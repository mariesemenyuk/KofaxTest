import java.util.HashMap;
import java.util.Map;

public class ASTree {
	
	private StringBuffer term;
	private int currentTermPos;
	Map<String, Integer> params = new HashMap<>();

	String literals = "abcdefghijklmnopqrstuvwxyz";
	
	private enum TokenType {
		NUMBER, VAR, PLUS, MINUS, MULTIPLY, DIVIDE, OPENING_BRACKET, CLOSING_BRACKET, EOT;
	}

	public AstNode buildTree(String s) throws ParserException {
		currentTermPos = 0;
		term = new StringBuffer(s);
		term = deleteSpaces(term);

		AstNode rootNode = parseAddSub();

		return rootNode;
	}

	private StringBuffer deleteSpaces(StringBuffer sbuf)
	{
		for (int i = 0; i < sbuf.length(); i++)
		{
			if (sbuf.charAt(i) == ' ')
			{
				sbuf.deleteCharAt(i);
				i--;
			}
		}
		return sbuf;
	}

	private TokenType getNextTokenType() throws ParserException
	{
		if (currentTermPos == term.length())
		{
			return TokenType.EOT;
		}
		else if (term.charAt(currentTermPos) >= '0' && term.charAt(currentTermPos) <= '9')
		{
			return TokenType.NUMBER;
		}
		else if(literals.indexOf(term.charAt(currentTermPos)) != -1) {
			return TokenType.VAR;
		}
		else if (term.charAt(currentTermPos) == '+')
		{
			currentTermPos++;
			return TokenType.PLUS;
		}
		else if (term.charAt(currentTermPos) == '-')
		{
			currentTermPos++;
			return TokenType.MINUS;
		}
		else if (term.charAt(currentTermPos) == '*')
		{
			currentTermPos++;
			return TokenType.MULTIPLY;
		}
		else if (term.charAt(currentTermPos) == '/')
		{
			currentTermPos++;
			return TokenType.DIVIDE;
		}
		else if (term.charAt(currentTermPos) == '(')
		{
			currentTermPos++;
			return TokenType.OPENING_BRACKET;
		}
		else if (term.charAt(currentTermPos) == ')')
		{
			currentTermPos++;
			return TokenType.CLOSING_BRACKET;
		}
		else
		{
			throw new ParserException(term.charAt(currentTermPos) + " is not a valid token");
		}
	}

	private void restoreLastTokenType()
	{
		currentTermPos--;
	}

    private AstNode parseMulDiv() throws ParserException
    {
    	AstNode rootNode = parseSimpleTerm();
    	TokenType nextToken = getNextTokenType();
    	
    	while (nextToken == TokenType.MULTIPLY || nextToken == TokenType.DIVIDE)
    	{
    		AstNode newRootNode;
    		if (nextToken == TokenType.MULTIPLY)
    		{
    			newRootNode = new AstNode('*');
    		}
    		else
    		{
    			newRootNode = new AstNode('/');
    		}
    		
    		newRootNode.leftTree = rootNode;
    		newRootNode.rightTree = parseSimpleTerm();
    		rootNode = newRootNode;
    		
    		nextToken = getNextTokenType();
    	}
    	restoreLastTokenType();
    	return rootNode;
    }

    private AstNode parseAddSub() throws ParserException
    {
    	AstNode rootNode = parseMulDiv();
    	TokenType nextToken = getNextTokenType();
    	
    	while (nextToken == TokenType.PLUS || nextToken == TokenType.MINUS)
    	{
    		AstNode newRootNode;
    		if (nextToken == TokenType.PLUS)
    		{
    			newRootNode = new AstNode('+');
    		}
    		else
    		{
    			newRootNode = new AstNode('-');
    		}

    		newRootNode.leftTree = rootNode;
    		newRootNode.rightTree = parseMulDiv();
    		rootNode = newRootNode;
    		
    		nextToken = getNextTokenType();
    	}
    	restoreLastTokenType();
    	return rootNode;
    }

    private AstNode parseSimpleTerm() throws ParserException
    {
    	TokenType nextToken = getNextTokenType();
    	AstNode rootNode = null;
    	if (nextToken == TokenType.NUMBER) {
    		int num = extractNextNumber();
    		rootNode = new AstNode(num);
    	}
		else if(nextToken == TokenType.VAR) {
			String var = extractNextVar();
			rootNode = new AstNode(var);
		}
    	else if (nextToken == TokenType.OPENING_BRACKET)
    	{
    		rootNode = parseAddSub();
    		nextToken = getNextTokenType();
    		if (nextToken != TokenType.CLOSING_BRACKET)
    		{
    			throw new ParserException("Missing closing bracket");
    		}
    	}
    	return rootNode;
    }

	private int extractNextNumber() throws ParserException
	{
		int posNumEnd;
        for (posNumEnd = currentTermPos; posNumEnd < term.length(); posNumEnd++)
        {
        	if ((term.charAt(posNumEnd) < '0' || term.charAt(posNumEnd) > '9'))
        		break;
        }
                
        if (posNumEnd > term.length())
            throw new ParserException(term + " is not a number.");
        
        String sub = term.substring(currentTermPos, posNumEnd);

        int x;
        try {
            x = Integer.parseInt(sub);
        } catch (NumberFormatException ex) {
            throw new ParserException("String to number parsing exception: " + sub);
        }
        
        currentTermPos = currentTermPos + (posNumEnd - currentTermPos);
        return x;
	}

	private String extractNextVar() throws ParserException
	{
		int posNumEnd;
		for (posNumEnd = currentTermPos; posNumEnd < term.length(); posNumEnd++)
		{
			boolean v1 = "0123456789".contains(String.valueOf(term.charAt(posNumEnd)));
			boolean v3 = !literals.contains(String.valueOf(term.charAt(posNumEnd)));
			if (v1 || v3)
				break;
		}

		if (posNumEnd > term.length())
			throw new ParserException(term + " is not a variable.");

		String var = term.substring(currentTermPos, posNumEnd);
		params.put(var, 0);

		currentTermPos = currentTermPos + (posNumEnd - currentTermPos);
		return var;
	}

    int calculateParserTree(AstNode tree, Map<String, Integer> params) throws ParserException {
        if (tree == null)
            throw new ParserException("Incorrect math expression");

		if(tree.leftTree != null){
			String value = String.valueOf(tree.leftTree.value);
			if(params.containsKey(tree.leftTree)) tree.leftTree = new AstNode(params.get(value));
		}

		if(tree.rightTree != null) {
			String value = String.valueOf(tree.rightTree.value);
			if (params.containsKey(value)) tree.rightTree = new AstNode(params.get(value));
		}

        if (tree.value.toString().equals("+"))
            return calculateParserTree(tree.leftTree, params) + calculateParserTree(tree.rightTree, params);
        else if (tree.value.toString().equals("-"))
            return calculateParserTree(tree.leftTree, params) - calculateParserTree(tree.rightTree, params);
        else if (tree.value.toString().equals("*"))
            return calculateParserTree(tree.leftTree, params) * calculateParserTree(tree.rightTree, params);
        else if (tree.value.toString().equals("/"))
            return calculateParserTree(tree.leftTree, params) / calculateParserTree(tree.rightTree, params);
        else {
			if(params.containsKey(tree.value.toString())) tree.value = params.get(String.valueOf(tree.value));
			return Integer.valueOf(tree.value.toString()).intValue();

		}
    }
}
