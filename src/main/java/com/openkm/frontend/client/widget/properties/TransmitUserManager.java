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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.service.OKMPropertyService;
import com.openkm.frontend.client.service.OKMPropertyServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.UserPopup;
import com.openkm.frontend.client.widget.organizationVTX.OrganizationVtxSelectPopup;
import com.openkm.frontend.client.widget.userVTX.UserVtxSelectPopup;

import java.util.HashSet;
import java.util.Set;

/**
 * CategoryManager
 *
 * @author jllort
 *
 */
public class TransmitUserManager {
	private final OKMPropertyServiceAsync propertyService = (OKMPropertyServiceAsync) GWT.create(OKMPropertyService.class);
	public static final int ORIGIN_FOLDER = 1;
	public static final int ORIGIN_DOCUMENT = 2;
	public static final int ORIGIN_MAIL = 3;

	private HTML categoriesImage;
	private HTML categoriesText;
	private FlexTable tableSubscribedCategories;
	private HorizontalPanel hPanelCategories;
	private boolean remove;
	private Set<GWTFolder> categories = new HashSet<GWTFolder>();
	private String path = "";
	private Object object;
	private int origin;
	private boolean removeCategoryEnabled = false;
	//private String orgs = "";

	//OrganizationVtxSelectPopup organizationVtxSelectPopup = new OrganizationVtxSelectPopup();
	UserVtxSelectPopup userVtxSelectPopup = new UserVtxSelectPopup();

	/**
	 * CategoryManager
	 */
	public TransmitUserManager(int origin) {
		this.origin = origin;
		tableSubscribedCategories = new FlexTable();
		hPanelCategories = new HorizontalPanel();
		categoriesText = new HTML("<br/><b>" + "Transmit user" + "</b>");
		categoriesImage = new HTML("<br/><span class=\"glyphicons glyphicons-transfer child-menuitem-glyphicon-renew\"></span>");
		categoriesImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				userVtxSelectPopup.show();
			}
		});

		hPanelCategories.add(categoriesText);
		hPanelCategories.add(new HTML("&nbsp;"));
		hPanelCategories.add(categoriesImage);
		hPanelCategories.setCellVerticalAlignment(categoriesText, HasAlignment.ALIGN_MIDDLE);

		setRowWordWarp(0, 0, true, tableSubscribedCategories);
		tableSubscribedCategories.setStyleName("okm-DisableSelect");
		categoriesImage.addStyleName("okm-Hyperlink");
		categoriesImage.setVisible(false);

		userVtxSelectPopup.setStyleName("okm-Popup");

	}

	/**
	 * setObject
	 *
	 * @param object
	 * @param remove
	 */
	public void setObject(Object object, boolean remove) {
		this.object = object;
		this.remove = remove;
		categories = new HashSet<GWTFolder>();
		if (object instanceof GWTDocument) {
			categories = ((GWTDocument) object).getCategories();
			path = ((GWTDocument) object).getPath();
		} else if (object instanceof GWTFolder) {
			categories = ((GWTFolder) object).getCategories();
			path = ((GWTFolder) object).getPath();
		} else if (object instanceof GWTMail) {
			categories = ((GWTMail) object).getCategories();
			path = ((GWTMail) object).getPath();
		}
	}

	/**
	 * getPanelCategories
	 *
	 * @return
	 */
	public Widget getPanelCategories() {
		return hPanelCategories;
	}

	/**
	 * getSubscribedCategoriesTable
	 *
	 * @return
	 */
	public FlexTable getSubscribedCategoriesTable() {
		return tableSubscribedCategories;
	}

	/**
	 * removeAllRows
	 */
	public void removeAllRows() {
		while (tableSubscribedCategories.getRowCount() > 0) {
			tableSubscribedCategories.removeRow(0);
		}
	}

	/**
	 * setVisible
	 *
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		categoriesImage.setVisible(visible);
	}

	/**
	 * drawAll
	 */
	public void drawAll() {
		for (GWTFolder category : categories) {
			drawCategory(category, remove);
		}
	}

	/**
	 * drawCategory
	 *
	 * @param category
	 */
	private void drawCategory(final GWTFolder category, boolean remove) {
		int row = tableSubscribedCategories.getRowCount();
		Anchor anchor = new Anchor();

		// Looks if must change icon on parent if now has no childs and properties with user security atention
		String path = category.getPath().substring(16); // Removes /okm:categories

		if (category.isHasChildren()) {
			anchor.setHTML(Util.imageItemHTMLIcon("glyphicons glyphicons-folder-new icon-green-renew", path, "top"));
		} else {
			anchor.setHTML(Util.imageItemHTMLIcon("glyphicons glyphicons-folder-open icon-green-renew", path, "top"));
		}

		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openPath(category.getPath(), null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");

		HTML delete = new HTML("<span class=\"glyphicons glyphicons-minus-sign icon-red-renew\"></span>");
		delete.setStyleName("okm-KeyMap-ImageHover");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch (origin) {
					case ORIGIN_FOLDER:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_CATEGORY_FOLDER);
						break;
					case ORIGIN_DOCUMENT:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_CATEGORY_DOCUMENT);
						break;
					case ORIGIN_MAIL:
						Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_CATEGORY_MAIL);
						break;
				}
				CategoryToRemove ctr = new CategoryToRemove(category, tableSubscribedCategories.getCellForEvent(event).getRowIndex());
				Main.get().confirmPopup.setValue(ctr);
				Main.get().confirmPopup.show();
			}
		});

		tableSubscribedCategories.setWidget(row, 0, anchor);
		if (remove && removeCategoryEnabled) {
			tableSubscribedCategories.setWidget(row, 1, delete);
		} else {
			tableSubscribedCategories.setWidget(row, 1, new HTML(""));
		}
		tableSubscribedCategories.setHTML(row, 2, category.getUuid());
		tableSubscribedCategories.getCellFormatter().setVisible(row, 2, false);
		setRowWordWarp(row, 1, true, tableSubscribedCategories);
	}

	/**
	 * Callback addCategory document
	 */
	final AsyncCallback<Object> callbackAddCategory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			if (object instanceof GWTDocument) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.CATEGORY_ADDED);
			} else if (object instanceof GWTFolder) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.fireEvent(HasFolderEvent.CATEGORY_ADDED);
			} else if (object instanceof GWTMail) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.fireEvent(HasMailEvent.CATEGORY_ADDED);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			Main.get().showError("AddCategory", caught);
		}
	};

	/**
	 * addCategory document
	 */
	public void addCategory(GWTFolder category) {
		if (!existCategory(category.getUuid())) {
			categories.add(category);
			drawCategory(category, remove);
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setCategories();
			propertyService.addCategory(path, category.getUuid(), callbackAddCategory);
		}
	}

	/**
	 * removeCategory document
	 */
	public void removeCategory(final String UUID) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setCategories();
		propertyService.removeCategory(path, UUID, new AsyncCallback<Object>() {
			public void onSuccess(Object result) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
				if (object instanceof GWTDocument) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.CATEGORY_REMOVED);
				} else if (object instanceof GWTFolder) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.fireEvent(HasFolderEvent.CATEGORY_REMOVED);
				} else if (object instanceof GWTMail) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.fireEvent(HasMailEvent.CATEGORY_REMOVED);
				}

				// removing row
				for (int row = 0; row < tableSubscribedCategories.getRowCount(); row++) {
					if (tableSubscribedCategories.getHTML(row, 2).equals(UUID)) {
						tableSubscribedCategories.removeRow(row);
						break;
					}
				}
				// removing category
				for (GWTFolder category : categories) {
					if (category.getUuid().equals(UUID)) {
						categories.remove(category);
						break;
					}
				}

			}

			public void onFailure(Throwable caught) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
				Main.get().showError("RemoveCategory", caught);
			}
		});
	}

	/**
	 * removeCategory
	 *
	 * @param category
	 */
	public void removeCategory(CategoryToRemove obj) {
		categories.remove(obj.getCategory());
		removeCategory(obj.getCategory().getUuid());
		tableSubscribedCategories.removeRow(obj.getRow());
	}

	/**
	 * existCategory
	 *
	 * @param Uuid
	 * @return
	 */
	private boolean existCategory(String Uuid) {
		boolean found = false;
		for (GWTFolder category : categories) {
			if (category.getUuid().equals(Uuid)) {
				found = true;
				break;
			}
		}
		return found;
	}

	public void transmit(String orgs, String docId) {
		RequestBuilder builder = new RequestBuilder(
				RequestBuilder.POST, Main.CONTEXT + "/services/rest/document/transmit?orgs=" + orgs + "&docId=" + docId);
		builder.setHeader("Accept", "application/json");
		try {
			builder.sendRequest(null, new RequestCallback() {
						@Override
						public void onResponseReceived(Request request,
													   Response response) {
							Window.alert("Đã chuyển văn bản đến các đơn vị!");
							Main.get().mainPanel.dashboard.userDashboard.getMustReadDocuments();
							Main.get().mainPanel.dashboard.userDashboard.isUserReadDoc(Main.get().workspaceUserProperties.getUser().getId(), docId);
						}

						@Override
						public void onError(Request request, Throwable throwable) {

						}
					}
			);
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}

	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		categoriesText.setHTML("<b>" + Main.i18n("document.categories") + "</b>");
	}

	/**
	 * showAddCategory
	 */
	public void showAddCategory() {
		categoriesImage.setVisible(true);
	}

	/**
	 * showRemoveCategory
	 */
	public void showRemoveCategory() {
		removeCategoryEnabled = true;
	}

	/**
	 * CategoryToRemove
	 *
	 * @author jllort
	 *
	 */
	public class CategoryToRemove {
		private GWTFolder category;
		private int row;

		public CategoryToRemove(GWTFolder category, int row) {
			this.category = category;
			this.row = row;
		}

		public GWTFolder getCategory() {
			return category;
		}

		public void setCategory(GWTFolder category) {
			this.category = category;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}
	}

	public void setOrgs(String orgs) {
		JsArrayNumber orgCheckeds = (JsArrayNumber) JsArrayNumber.createArray();
		JsArrayString orgPathTrace = (JsArrayString) JsArrayString.createArray();
		JSONValue jsonValue = JSONParser.parseStrict(orgs);
		JSONArray jsonArray = jsonValue.isArray();
		for(int i=0; i<jsonArray.size(); ++i) {
			JSONArray jsonArray_ = jsonArray.get(i).isArray();
			orgCheckeds.push(jsonArray_.get(0).isNumber().doubleValue());
			orgPathTrace.push(jsonArray_.get(4).isString().stringValue());
		}

		//organizationVtxSelectPopup.setOrgCheckeds(orgCheckeds, orgPathTrace);
	}

}
