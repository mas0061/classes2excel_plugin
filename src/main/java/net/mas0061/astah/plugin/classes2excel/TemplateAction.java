package net.mas0061.astah.plugin.classes2excel;


import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
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
	        projectAccessor.getProject();
	        readClasses();
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

    private void readClasses() {
        ReadClasses reader = new ReadClasses();
//        List<String> classList = reader.getClassesName();
        List<String> classList = reader.getClassAnnotations();
        String className = "";
        for (String name : classList) {
            System.out.println(name);
            className += name + ", ";
        }
        JOptionPane.showMessageDialog(currentWindow.getParent(), className);
    }

}
