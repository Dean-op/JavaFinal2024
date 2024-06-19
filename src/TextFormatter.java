import javax.swing.JTextArea;

public class TextFormatter {
    public void formatText(JTextArea textArea, String formatType) {
        String selectedText = textArea.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            return;
        }
        String formattedText = switch (formatType) {
            case "一级标题" -> "# " + selectedText;
            case "二级标题" -> "## " + selectedText;
            case "三级标题" -> "### " + selectedText;
            case "四级标题" -> "#### " + selectedText;
            case "正文" -> selectedText;
            case "代码块" -> "```\n" + selectedText + "\n```";
            case "引用" -> "> " + selectedText;
            case "斜体" -> "*" + selectedText + "*";
            case "粗体" -> "**" + selectedText + "**";
            default -> "";
        };
        textArea.replaceSelection(formattedText);
    }
}
