//NoteBookApp类
import javax.swing.SwingUtilities;
public class NotebookApp {
    public static void main(String[] args) {
        // 启动应用程序入口
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
