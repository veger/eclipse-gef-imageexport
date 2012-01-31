/*
 * This file is part of ImageExport.
 *
 * ImageExport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImageExport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ImageExport.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.utwente.ce.imageexport;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * Class that shows an exception in a Eclipse {@link IconAndMessageDialog}.
 * </p>
 * <p>
 * This class is loosely based on {@link org.eclipse.jface.dialogs.ErrorDialog ErrorDialog}
 * </p>
 */
public class ExceptionErrorDialog extends IconAndMessageDialog
{
    /** The Details button. */
    private Button detailsButton;

    /** The title of the dialog. */
    private String title;

    /** The SWT list control that displays the error details. */
    private Text text;

    /** Indicates whether the error details viewer is currently created. */
    private boolean listCreated = false;

    /** The status object. */
    private IStatus status;

    /**
     * @param parentShell the shell under which to create this dialog
     * @param dialogTitle the title to use for this dialog, or <code>null</code> to indicate that the default title
     *            should be used
     * @param message the message to show in this dialog, or <code>null</code> to indicate that the error's message
     *            should be shown as the primary message
     * @param status the error to show to the user
     */
    public ExceptionErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status)
    {
        super(parentShell);
        this.title = dialogTitle == null ? JFaceResources.getString("Problem_Occurred") : //$NON-NLS-1$
                dialogTitle;
        this.message = message == null ? status.getMessage() : JFaceResources.format(
                "Reason", new Object[] { message, status.getMessage() }); //$NON-NLS-1$
        this.status = status;
    }

    @Override
    protected void buttonPressed(int id)
    {
        if (id == IDialogConstants.DETAILS_ID)
        {
            // Details button got pressed
            toggleDetailsArea();
        }
        else
        {
            super.buttonPressed(id);
        }
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(title);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        // create OK and Details buttons
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
    }

    /** Create the area for extra error support information. */
    private void createSupportArea(Composite parent)
    {
        Composite supportArea = new Composite(parent, SWT.NONE);
        GridData supportData = new GridData(SWT.FILL, SWT.FILL, true, true);
        supportData.verticalSpan = 3;
        supportArea.setLayoutData(supportData);
        if (supportArea.getLayout() == null)
        {
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            supportArea.setLayout(layout); // Give it a default layout if one isn't set
        }
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        createMessageArea(composite);
        createSupportArea(parent);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        composite.setLayout(layout);
        GridData childData = new GridData(GridData.FILL_BOTH);
        childData.horizontalSpan = 2;
        childData.grabExcessVerticalSpace = false;
        composite.setLayoutData(childData);
        composite.setFont(parent.getFont());

        return composite;
    }

    @Override
    protected void createDialogAndButtonArea(Composite parent)
    {
        super.createDialogAndButtonArea(parent);
        if (this.dialogArea instanceof Composite)
        {
            // Create a label if there are no children to force a smaller layout
            Composite dialogComposite = (Composite) dialogArea;
            if (dialogComposite.getChildren().length == 0)
            {
                new Label(dialogComposite, SWT.NULL);
            }
        }
    }

    @Override
    protected Image getImage()
    {
        if (status != null)
        {
            if (status.getSeverity() == IStatus.WARNING)
            {
                return getWarningImage();
            }
            if (status.getSeverity() == IStatus.INFO)
            {
                return getInfoImage();
            }
        }
        // If it was not a warning or an info then return the error image
        return getErrorImage();
    }

    /**
     * Create this dialog's drop-down text component. The text is displayed after the user presses details button. It is
     * developer responsibility to display details button if and only if there is some content on drop down list. The
     * visibility of the details button is controlled by {@link #shouldShowDetailsButton()}, which should also be
     * overridden together with this method.
     * 
     * @param parent the parent composite
     * @return the drop-down list component
     */
    protected Text createDropDownText(Composite parent)
    {
        text = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        // fill the list
        populateList(text, status);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
                | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
        data.horizontalSpan = 2;
        text.setLayoutData(data);
        text.setFont(parent.getFont());
        text.setEditable(false);
        listCreated = true;
        return text;
    }

    /**
     * Opens an error dialog to display the given error.
     * 
     * @param parentShell the parent shell of the dialog, or <code>null</code> if none
     * @param title the title to use for this dialog, or <code>null</code> to indicate that the default title should be
     *            used
     * @param message the message to show in this dialog, or <code>null</code> to indicate that the error's message
     *            should be shown as the primary message
     * @param status the error to show to the user
     */
    public static void openError(Shell parentShell, String title, String message, IStatus status)
    {
        ExceptionErrorDialog dialog = new ExceptionErrorDialog(parentShell, title, message, status);
        dialog.open();
    }

    /**
     * Populate the list with the messages from the given status. Traverse the children of the status deeply and also
     * traverse CoreExceptions that appear in the status.
     * 
     * @param listToPopulate the list to populate
     * @param buildingStatus the status being displayed
     */
    private void populateList(Text listToPopulate, IStatus buildingStatus)
    {
        Throwable t = buildingStatus.getException();
        // Include low-level exception message
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String message = sw.getBuffer().toString();
        if (message == null)
        {
            message = t.toString();
        }
        listToPopulate.append(message);
    }

    /** Toggles the unfolding of the details area. This is triggered by the user pressing the details button. */
    private void toggleDetailsArea()
    {
        Point windowSize = getShell().getSize();
        Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (listCreated)
        {
            text.dispose();
            listCreated = false;
            detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
        }
        else
        {
            text = createDropDownText((Composite) getContents());
            detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
            getContents().getShell().layout();
        }
        Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
    }

    /**
     * Show the details portion of the dialog if it is not already visible. This method will only work when it is
     * invoked after the control of the dialog has been set. In other words, after the <code>createContents</code>
     * method has been invoked and has returned the control for the content area of the dialog. Invoking the method
     * before the content area has been set or after the dialog has been disposed will have no effect.
     * 
     * @since 3.1
     */
    protected final void showDetailsArea()
    {
        if (!listCreated)
        {
            Control control = getContents();
            if (control != null && !control.isDisposed())
            {
                toggleDetailsArea();
            }
        }
    }

    protected boolean isResizable()
    {
        return true;
    }
}
