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

package com.openkm.frontend.client.widget.userVTX;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OkmConstants;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.categories.FolderSelectTree;
import com.openkm.frontend.client.widget.categories.Status;
import com.openkm.frontend.client.widget.filebrowser.ExtendedScrollTable;
import org.docx4j.wml.U;

import java.util.ArrayList;
import java.util.List;

public class UserVtxSelectPopup extends DialogBox {

	private static class User {
		private final String id;
		private final String fullName;
		private final String email;

		public User(String id, String fullName, String email) {
			this.id = id;
			this.fullName = fullName;
			this.email = email;
		}
	}

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	public FlexTable table;


	JsArrayNumber orgCheckeds = (JsArrayNumber) JsArrayNumber.createArray();
	JsArrayString orgPathTrace = (JsArrayString) JsArrayString.createArray();

	public List<String> userChecked = new ArrayList<>();


	public UserVtxSelectPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		status = new Status();
		status.setStyleName("okm-StatusPopup");

		vPanel = new VerticalPanel();
		vPanel.setWidth("600px");
		vPanel.setHeight("400px");
		hPanel = new HorizontalPanel();

		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("600px", "400px");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");

		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");

		scrollDirectoryPanel.add(verticalDirectoryPanel);

		cancelButton = new Button(OkmConstants.ICON_NO_BUTTON + Main.i18n("button.close"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		actionButton = new Button(OkmConstants.ICON_ADD_BUTTON + Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAction();
			}
		});

		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollDirectoryPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50px");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDirectoryPanel, "250px");

		cancelButton.setStyleName("okm-NoButton");
		cancelButton.addStyleName("btn");
		cancelButton.addStyleName("btn-warning");
		actionButton.setStyleName("okm-AddButton");
		actionButton.addStyleName("btn");
		actionButton.addStyleName("btn-success");

		drawUserTable();
		super.hide();
		setWidget(vPanel);
	}


	public void drawUserTable() {
		/*CellTable<User> table = new CellTable<User>();

		// Add a text column to show the id.
		TextColumn<User> idColumn =
				new TextColumn<User>() {
					@Override
					public String getValue(User object) {
						return object.id;
					}
				};
		table.addColumn(idColumn, "Code");

		// Add a text column to show the name.
		TextColumn<User> nameColumn =
				new TextColumn<User>() {
					@Override
					public String getValue(User object) {
						return object.fullName;
					}
				};
		table.addColumn(nameColumn, "Name");

		// Add a text column to show the email.
		TextColumn<User> emailColumn
				= new TextColumn<User>() {
			@Override
			public String getValue(User object) {
				return object.email;
			}
		};
		table.addColumn(emailColumn, "Email");

		List<User> userList = new ArrayList<>();*/

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Main.CONTEXT + "/services/rest/user/getAllUser?userSearch=&notInOrg=0");
		builder.setHeader("Accept", "application/json");

		try {
			builder.sendRequest(null, new RequestCallback() {
						@Override
						public void onResponseReceived(Request request,
													   Response response) {
							JSONValue jsonValue = JSONParser.parseStrict(response.getText());
							JSONArray jsonArray = jsonValue.isArray();

							table = new FlexTable();
							table.setCellSpacing(5);
							table.setCellPadding(3);
							table.setWidth("100%");
							table.addStyleName("flex-table-user-transmit");


							table.setWidget(0, 0, new HTML("<b>Code</b>"));
							table.setWidget(0, 1, new HTML("<b>Full name</b>"));
							table.setWidget(0, 2, new HTML("<b>Email</b>"));
							table.setWidget(0, 3, new HTML(""));
							for (int i = 0; i < jsonArray.size(); ++i) {
								String code = jsonArray.get(i).isArray().get(1).isString().stringValue();
								String fullName = jsonArray.get(i).isArray().get(0).isString().stringValue();
								String email = jsonArray.get(i).isArray().get(2).isString().stringValue();
								table.setWidget(i+1, 0, new Label(code));
								table.setWidget(i+1, 1, new Label(fullName));
								table.setWidget(i+1, 2, new Label(email));

								CheckBox userCheckbox = new CheckBox();

								userCheckbox.addClickHandler(
										new ClickHandler() {
											@Override
											public void onClick(ClickEvent event) {
												boolean checked = ((CheckBox) event.getSource()).getValue();
												Window.alert("check:" + checked);
												if(checked) {
													userChecked.add(code);
												} else {
													userChecked.remove(code);
												}
											}
										});
								table.setWidget(i+1, 3, userCheckbox);
							}

							verticalDirectoryPanel.add(table);


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
	 * Executes the action
	 */
	public void executeAction() {
		StringBuilder usrs = new StringBuilder();
		for (int i = 0; i < userChecked.size(); ++i) {
			usrs.append(",").append(userChecked.get(i));
		}
		usrs.deleteCharAt(0);
		Window.alert(usrs.toString());
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("categories.folder.select.label"));
		cancelButton.setHTML(OkmConstants.ICON_NO_BUTTON + Main.i18n("button.close"));
		actionButton.setHTML(OkmConstants.ICON_ADD_BUTTON + Main.i18n("button.add"));
	}

	/**
	 * Shows the popup
	 */
	public void show() {
		initButtons();
		int left = (Window.getClientWidth() - 600) / 2;
		int top = (Window.getClientHeight() - 400) / 2;
		setPopupPosition(left, top);
		setText("Add user");

		// Resets to initial tree value
		folderSelectTree.reset();
		super.show();
	}

	/**
	 * Enables or disables move button
	 *
	 * @param enable
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);
	}

	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(true);
	}

}
