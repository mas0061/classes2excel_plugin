package net.mas0061.astah.plugin.classes2excel;


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import net.mas0061.astah.plugin.classes2excel.read.ElementWithAnnotation;
import net.mas0061.astah.plugin.classes2excel.read.FileExporter;
import net.mas0061.astah.plugin.classes2excel.read.ReadClasses;

import javax.swing.*;
import java.util.List;

public class TemplateAction implements IPluginActionDelegate {
    private IWindow currentWindow;

	public Object run(IWindow window) throws UnExpectedException {
	    try {
	        currentWindow = window;
	        AstahAPI api = AstahAPI.getAstahAPI();
	        ProjectAccessor projectAccessor = api.getProjectAccessor();
	        IModel project = projectAccessor.getProject();

            ReadClasses reader = new ReadClasses(projectAccessor, project);
            List<ElementWithAnnotation> classList = reader.getClassStructure();
            List<ElementWithAnnotation> attrList = reader.getClassesAttributes();
            String outFileName = System.getProperty("user.home") + "/Desktop/outAll.xlsx";
            new FileExporter().exportAllListExcel(classList, attrList, outFileName);

	        JOptionPane.showMessageDialog(currentWindow.getParent(), "Excelエクスポート終了");
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
