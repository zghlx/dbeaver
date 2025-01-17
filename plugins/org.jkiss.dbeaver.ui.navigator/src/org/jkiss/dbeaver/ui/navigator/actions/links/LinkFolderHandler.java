/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.ui.navigator.actions.links;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.utils.GeneralUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LinkFolderHandler extends CreateLinkHandler {

    @Override
    protected Path[] selectTargets(ExecutionEvent event) {
        Shell shell = HandlerUtil.getActiveShell(event);
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
        String folder = dialog.open();
        if (folder == null) {
            return NO_TARGETS;
        }
        Path folderPath = Paths.get(folder);
        return new Path[] { folderPath };
    }

    @Override
    protected IStatus createLink(IContainer container, IProgressMonitor monitor, Path... targets) {
        return createLinkedFolders(container, monitor, targets);
    }

    /**
     * Bulk operation to create several linked folders
     *
     * @param container
     * @param monitor
     * @param paths
     * @return
     */
    public static IStatus createLinkedFolders(IContainer container, IProgressMonitor monitor, Path... paths) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        CreateLinkedFoldersRunnable action = new CreateLinkedFoldersRunnable(container, paths);
        try {
            workspace.run(action, monitor);
        } catch (CoreException e) {
            return e.getStatus();
        } catch (Throwable e) {
            return GeneralUtils.makeErrorStatus(action.composeErrorMessage(container, paths), e);
        }
        return Status.OK_STATUS;
    }

}