import javax.swing.SwingUtilities;
/*
* Author：软件工程2203李国琦
*
* */
public class NotebookApp {
    public static void main(String[] args) {
        // 启动应用程序入口
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
