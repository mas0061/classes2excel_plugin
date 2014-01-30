package net.mas0061.astah.plugin.classes2excel;


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.sun.codemodel.internal.JOp;import net.mas0061.astah.plugin.classes2excel.read.ElementWithAnnotation;
import net.mas0061.astah.plugin.classes2excel.read.FileExporter;
import net.mas0061.astah.plugin.classes2excel.read.ReadClasses;

import javax.swing.*;import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.List;

public class TemplateAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
	    try {
	        AstahAPI api = AstahAPI.getAstahAPI();
	        ProjectAccessor projectAccessor = api.getProjectAccessor();
	        IModel project = projectAccessor.getProject();

	        JFileChooser chooser = new JFileChooser();
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        chooser.setMultiSelectionEnabled(false);
	        chooser.setFileFilter(new FileNameExtensionFilter("Excelファイル(*.xlsx)", "xlsx"));

	        int selected = chooser.showSaveDialog(window.getParent());

	        if (selected == JFileChooser.CANCEL_OPTION) {
			    JOptionPane.showMessageDialog(window.getParent(), "ファイルを選択して下さい。", "Warning", JOptionPane.WARNING_MESSAGE);
			    return null;
            }

            File saveFile = chooser.getSelectedFile();
            
            if (saveFile.exists()) {
                int overWrite = JOptionPane.showConfirmDialog(window.getParent(), "既にファイルが存在します。上書きしますか？",
                    "ファイル上書き確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (overWrite == JOptionPane.NO_OPTION) {
                    return null;
                }
            }

            ReadClasses reader = new ReadClasses(projectAccessor, project);
            List<ElementWithAnnotation> classList = reader.getClassStructure();
            List<ElementWithAnnotation> attrList = reader.getClassesAttributes();
            new FileExporter().exportAllListExcel(classList, attrList, saveFile);

	        JOptionPane.showMessageDialog(window.getParent(), "Excelファイルへのエクスポートが正常に完了しました。");
	    } catch (ProjectNotFoundException e) {
	        String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE); 
	    } catch (Exception e) {
	        e.printStackTrace();
	    	JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE); 
	        throw new UnExpectedException();
	    }
	    return null;
	}

}
