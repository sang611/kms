/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.widget.template;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Template menu
 *
 * @author jllort
 *
 */
public class TemplateMenu extends MenuBase {

	private ToolBarOption toolBarOption;
	private MenuBar dirMenu;
	private MenuItem create;
	private MenuItem remove;
	private MenuItem rename;
	private MenuItem move;
	private MenuItem copy;
	private MenuItem export;

	public TemplateMenu() {
		toolBarOption = new ToolBarOption();
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		dirMenu = new MenuBar(true);
		create = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-folder-plus", Main.i18n("tree.menu.directory.create")), true, addFolder);
		create.addStyleName("okm-MenuItem");
		dirMenu.addItem(create);
		remove = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-bin", Main.i18n("tree.menu.directory.remove")), true, delFolder);
		remove.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(remove);
		rename = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-text-color", Main.i18n("tree.menu.directory.rename")), true, renFolder);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		move = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-copy", Main.i18n("tree.menu.directory.move")), true, moveFolder);
		move.addStyleName("okm-MenuItem");
		dirMenu.addItem(move);
		copy = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-duplicate", Main.i18n("tree.menu.directory.copy")), true, copyFolder);
		copy.addStyleName("okm-MenuItem");
		dirMenu.addItem(copy);
		export = new MenuItem(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-cargo", Main.i18n("tree.menu.export")), true, exportToFile);
		export.addStyleName("okm-MenuItem");
		dirMenu.addItem(export);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}

	// Command menu to add a new Directory
	Command addFolder = new Command() {
		public void execute() {
			if (toolBarOption.createFolderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFolderDirectory();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to delete a new Directory
	Command delFolder = new Command() {
		public void execute() {
			if (toolBarOption.deleteOption) {
				Main.get().activeFolderTree.confirmDelete();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to delete a new Directory
	Command renFolder = new Command() {
		public void execute() {
			if (toolBarOption.renameOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRename();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to refresh actual Directory
	Command moveFolder = new Command() {
		public void execute() {
			if (toolBarOption.moveOption) {
				Main.get().activeFolderTree.move();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to refresh actual Directory
	Command copyFolder = new Command() {
		public void execute() {
			if (toolBarOption.copyOption) {
				Main.get().activeFolderTree.copy();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	// Command menu to set default home
	Command exportToFile = new Command() {
		public void execute() {
			if (toolBarOption.exportOption) {
				Main.get().activeFolderTree.exportFolderToFile();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};

	@Override
	public void evaluateMenuOptions() {
		if (toolBarOption.createFolderOption) {
			enable(create);
		} else {
			disable(create);
		}
		if (toolBarOption.deleteOption) {
			enable(remove);
		} else {
			disable(remove);
		}
		if (toolBarOption.renameOption) {
			enable(rename);
		} else {
			disable(rename);
		}
		if (toolBarOption.moveOption) {
			enable(move);
		} else {
			disable(move);
		}
		if (toolBarOption.copyOption) {
			enable(copy);
		} else {
			disable(copy);
		}
		if (toolBarOption.exportOption) {
			enable(export);
		} else {
			disable(export);
		}
	}

	@Override
	public void langRefresh() {
		create.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-folder-plus", Main.i18n("tree.menu.directory.create")));
		remove.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-bin", Main.i18n("tree.menu.directory.remove")));
		rename.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-text-color", Main.i18n("tree.menu.directory.rename")));
		move.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-copy", Main.i18n("tree.menu.directory.move")));
		copy.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-duplicate", Main.i18n("tree.menu.directory.copy")));
		export.setHTML(Util.menuHTMLWithGlyphicon("glyphicons glyphicons-cargo", Main.i18n("tree.menu.export")));
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isCreateFolderOption()) {
			dirMenu.removeItem(create);
		}
		if (!option.isDeleteOption()) {
			dirMenu.removeItem(remove);
		}
		if (!option.isRenameOption()) {
			dirMenu.removeItem(rename);
		}
		if (!option.isMoveOption()) {
			dirMenu.removeItem(move);
		}
		if (!option.isCopyOption()) {
			dirMenu.removeItem(copy);
		}
		if (!option.isExportOption()) {
			dirMenu.removeItem(export);
		}
		create.setVisible(option.isCreateFolderOption());
		remove.setVisible(option.isDeleteOption());
		rename.setVisible(option.isRenameOption());
		move.setVisible(option.isMoveOption());
		copy.setVisible(option.isCopyOption());
		export.setVisible(option.isExportOption());
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		this.toolBarOption = toolBarOption;
		evaluateMenuOptions();
	}

	@Override
	public void disableAllOptions() {
		toolBarOption = new ToolBarOption();
		evaluateMenuOptions();
	}

	@Override
	public void enableAddPropertyGroup() {
	}

	@Override
	public void disableAddPropertyGroup() {
	}

	@Override
	public void disablePdfMerge() {
	}

	@Override
	public void enablePdfMerge() {
	}
}