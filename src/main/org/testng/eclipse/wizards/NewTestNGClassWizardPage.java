package org.testng.eclipse.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.testng.eclipse.ui.util.Utils;
import org.testng.eclipse.ui.util.Utils.Widgets;

import java.util.HashMap;
import java.util.Map;

/**
 * Generate a new TestNG class.
 */
public class NewTestNGClassWizardPage extends WizardPage {
  private ISelection m_selection;
  private Text m_sourceFolderText;
  private Text m_packageNameText;
  private Text m_classNameText;
  private Button m_generateXmlFile;
  private Text m_xmlFilePath;

  private Map<String, Button> m_annotations = new HashMap<String, Button>();
  public static final String[] ANNOTATIONS = new String[] {
    "BeforeMethod", "AfterMethod", "DataProvider",
    "BeforeClass", "AfterClass", "",
    "BeforeTest",  "AfterTest", "",
    "BeforeSuite", "AfterSuite", ""
  };

  public NewTestNGClassWizardPage(ISelection selection) {
    super("TestNG class");
    setTitle("TestNG class");
    setDescription("This wizard creates a new TestNG class.");
    m_selection = selection;
  }

  /**
   * @see IDialogPage#createControl(Composite)
   */
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    container.setLayout(layout);

    createTop(container);
    createBottom(container);

    initialize();
    dialogChanged();
    setControl(container);
  }

  private void createBottom(Composite parent) {
    //
    // Annotations
    //
    {
      Group g = new Group(parent, SWT.SHADOW_ETCHED_OUT);
      g.setText("Annotations");
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      g.setLayoutData(gd);

      GridLayout layout = new GridLayout();
      g.setLayout(layout);
      layout.numColumns = 3;

      for (String label : ANNOTATIONS) {
        if ("".equals(label)) {
          new Label(g, SWT.NONE);
        } else {
          Button b = new Button(g, "".equals(label) ? SWT.None : SWT.CHECK);
          m_annotations.put(label, b);
          b.setText("@" + label);
        }
      }
    }

    //
    // Generate XML file
    //
    {
//      SelectionListener browseListener = new SelectionListener() {
//        public void widgetDefaultSelected(SelectionEvent e) {
//          m_xmlFilePath.setEnabled(((Button) e.getSource()).getSelection());
//        }
//        public void widgetSelected(SelectionEvent e) {
//          FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
//          m_xmlFilePath.setText(fileDialog.open());
//        }
//      };

      SelectionListener checkListener = new SelectionListener() {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
          dialogChanged();
        }
      };

      Widgets w = Utils.createTextBrowseControl(parent,
        "TestNG.newClass.generateXmlSuiteFile", "TestNG.newClass.suitePath",
        null /* no browse */,
        checkListener,
        null, false /* disabled by default */);
      m_generateXmlFile = w.radio;
      m_xmlFilePath = w.text;
//
//      Label l = new Label(g, SWT.NULL);
//      l.setText("File name");
//      Text t = new Text(g, SWT.BORDER | SWT.SINGLE);
//      t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      Button browse = new Button(g, SWT.PUSH);
//      browse.setText("Browse");

//      FieldBrowse fb = new FieldBrowse(g, "File name") {
//
//        @Override
//        protected void onBrowse() {
//        }
//
//        @Override
//        protected void onTextChanged() {
//        }
//        
//      };
//      m_generateXmlFile = new Button(g, SWT.CHECK);
//      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//      gridData.horizontalSpan = 3;
//      g.setLayoutData(gridData);
//      m_generateXmlFile.addSelectionListener(new SelectionListener() {
//        public void widgetDefaultSelected(SelectionEvent e) {
//        }
//
//        public void widgetSelected(SelectionEvent e) {
//          m_xmlFilePath.setEnabled(((Button) e.getSource()).getSelection(), g);
//        }
//      });

    }
  }

  private void createTop(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    {
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      container.setLayoutData(gd);
      GridLayout layout = new GridLayout();
      layout.numColumns = 3;
      container.setLayout(layout);
    }

    //
    // Source folder
    //
    {
      Label label = new Label(container, SWT.NULL);
      label.setText("&Source folder:");
      m_sourceFolderText = new Text(container, SWT.BORDER | SWT.SINGLE);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      m_sourceFolderText.setLayoutData(gd);
      m_sourceFolderText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          dialogChanged();
        }
      });
      Button button = new Button(container, SWT.PUSH);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          handleBrowse();
        }
      });
    }

    //
    // Package name
    //
    {
      Label label = new Label(container, SWT.NULL);
      label.setText("&Package name:");
      m_packageNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
      m_packageNameText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          dialogChanged();
        }
      });
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      m_packageNameText.setLayoutData(gd);
      Button button = new Button(container, SWT.PUSH);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          handleBrowsePackages();
        }
      });
    }

    //
    // Class name
    //
    {
      Label label = new Label(container, SWT.NULL);
      label.setText("&Class name:");
      m_classNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      m_classNameText.setLayoutData(gd);
      m_classNameText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          dialogChanged();
        }
      });
    }
  }

//  public void _createControl(Composite parent) {
//    Composite container = new Composite(parent, SWT.NULL);
//    GridLayout layout = new GridLayout();
//    container.setLayout(layout);
//    layout.numColumns = 3;
//    layout.verticalSpacing = 9;
//    Label label = new Label(container, SWT.NULL);
//    label.setText("&Class name:");
//
//    m_containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
//    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//    m_containerText.setLayoutData(gd);
//    m_containerText.addModifyListener(new ModifyListener() {
//      public void modifyText(ModifyEvent e) {
//        dialogChanged();
//      }
//    });
//
//    Button button = new Button(container, SWT.PUSH);
//    button.setText("Browse...");
//    button.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent e) {
//        handleBrowse();
//      }
//    });
//    label = new Label(container, SWT.NULL);
//    label.setText("&File name:");
//
//    m_fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
//    gd = new GridData(GridData.FILL_HORIZONTAL);
//    m_fileText.setLayoutData(gd);
//    m_fileText.addModifyListener(new ModifyListener() {
//      public void modifyText(ModifyEvent e) {
//        dialogChanged();
//      }
//    });
//    initialize();
//    dialogChanged();
//    setControl(container);
//  }

  /**
   * Tests if the current workbench selection is a suitable container to use.
   */
  private void initialize() {
    if (m_selection != null && m_selection.isEmpty() == false
        && m_selection instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) m_selection;
      if (ssel.size() > 1)
        return;
      Object obj = ssel.getFirstElement();
      if (obj instanceof IResource) {
        IContainer container;
        if (obj instanceof IContainer) {
          container = (IContainer) obj;
        } else {
          container = ((IResource) obj).getParent();
        }
        m_sourceFolderText.setText(container.getFullPath().toString());
      }
    }
    m_classNameText.setText("NewTest");
  }

  private void handleBrowsePackages() {
  }

  /**
   * Uses the standard container selection dialog to choose the new value for
   * the container field.
   */
  private void handleBrowse() {
    ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
        .getWorkspace().getRoot(), false, "Select new file container");
    if (dialog.open() == ContainerSelectionDialog.OK) {
      Object[] result = dialog.getResult();
      if (result.length == 1) {
        m_sourceFolderText.setText(((Path) result[0]).toString());
      }
    }
  }

  /**
   * Ensures that both text fields are set.
   */
  private void dialogChanged() {
    IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(
        new Path(getSourceFolder()));
    String className = getClassName();

    if (container == null || (container.getType() &
        (IResource.ROOT | IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus("The source directory must exist");
      return;
    }
    if (getPackageName().length() == 0) {
      updateStatus("The package must be specified");
      return;
    }
    if (!container.isAccessible()) {
      updateStatus("Project must be writable");
      return;
    }
    if (className.length() == 0) {
      updateStatus("Class name must be specified");
      return;
    }
    if (className.replace('\\', '/').indexOf('/', 1) > 0) {
      updateStatus("Class name must be valid");
      return;
    }
    if (m_generateXmlFile.getSelection() && Utils.isEmpty(m_xmlFilePath.getText())) {
      updateStatus("You need to specify the location of the XML file");
      return;
    }

    int dotLoc = className.lastIndexOf('.');
    if (dotLoc != -1) {
      String ext = className.substring(dotLoc + 1);
      if (ext.equalsIgnoreCase("java") == false) {
        updateStatus("File extension must be \"java\"");
        return;
      }
    }
    updateStatus(null);
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getSourceFolder() {
    return m_sourceFolderText.getText();
  }

  public String getXmlFile() {
    return m_generateXmlFile.getSelection() ? m_xmlFilePath.getText() : null;
  }

  public String getPackageName() {
    return m_packageNameText.getText();
  }

  public String getClassName() {
    return m_classNameText.getText();
  }

  public boolean containsAnnotation(String annotation) {
    Button b = m_annotations.get(annotation);
    return b.getSelection();
  }

  public String getPackage() {
    return m_packageNameText.getText();
  }
}