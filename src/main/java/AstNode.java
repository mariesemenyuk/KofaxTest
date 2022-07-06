public class AstNode {

        public AstNode leftTree;
        public AstNode rightTree;
        public Object  value;

        public AstNode(AstNode l, AstNode r, Object v)
        {
            leftTree = l;
            rightTree = r;
            value = v;
        }

        public AstNode(Object l, Object r, Object v)
        {
            leftTree = new AstNode(l);
            rightTree = new AstNode(r);
            value = v;
        }

        public AstNode()
        {
            leftTree = rightTree = null;
            value = null;
        }

        public AstNode(Object v)
        {
            leftTree = rightTree = null;
            value = v;
        }

        public String prettyPrint() {
                StringBuilder output = new StringBuilder();
                inOrderRec(this, output);
                return output.toString();
        }

        private void inOrderRec(AstNode root, StringBuilder output)
        {
            if (root != null) {
                int counter = String.valueOf(root.value).length();
                if(root.leftTree != null){
                    printTree(root.leftTree, true, "", counter, output);
                }
                printNodeValue(root, output);
                if(root.rightTree != null){
                    printTree(root.rightTree, false, "", counter, output);
                }
            }
        }

        private void printNodeValue(AstNode root, StringBuilder output) {
            if (root != null) {
                output.append(root.value);
            }

            if(root.leftTree != null && root.rightTree != null){
                output.append('┤');
            } else if(root.leftTree != null){
                output.append('┘');
            } else if(root.rightTree != null){
                output.append('┐');
            }
            output.append('\n');
        }

        private void printTree(AstNode root, boolean isLeft, String indent, int counter, StringBuilder output) {
            if (root.leftTree != null) {
                while(counter != 0) {
                    indent += " ";
                    counter--;
                }
                counter += String.valueOf(root.value).length();
                printTree(root.leftTree, true, indent + (isLeft ? " " : "│"), counter, output);
            } else {
                while(counter != 0) {
                    indent += " ";
                    counter--;
                }
            }

            output.append(indent);

            if (isLeft) {
                output.append("┌");
            } else {
                output.append("└");
            }
            printNodeValue(root, output);

            if (root.rightTree != null) {
                indent += (isLeft ? "│" : " " );
                counter = String.valueOf(root.value).length();
                printTree(root.rightTree, false, indent, counter, output);
            }
        }
}
