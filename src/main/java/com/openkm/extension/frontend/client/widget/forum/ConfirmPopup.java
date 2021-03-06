/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.extension.frontend.client.widget.forum;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.extension.frontend.client.util.OkmConstants;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

/**
 * Confirm panel
 *
 * @author jllort
 */
public class ConfirmPopup extends DialogBox {
	public static final int NO_ACTION = 0;
	public static final int CONFIRM_DELETE_FORUM = 1;
	public static final int CONFIRM_DELETE_POST = 2;

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML text;
	private Button cancelButton;
	private Button acceptButton;
	private int action = 0;
	private long id = 0;
	private ForumController controller;

	/**
	 * Confirm popup
	 */
	public ConfirmPopup() {
		// Establishes auto-close when click outside
		super(false, true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		text = new HTML();
		text.setStyleName("okm-NoWrap");

		cancelButton = new Button(OkmConstants.ICON_NO_BUTTON + GeneralComunicator.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		acceptButton = new Button(OkmConstants.ICON_YES_BUTTON + GeneralComunicator.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				execute();
				hide();
			}
		});

		vPanel.setWidth("300px");
		vPanel.setHeight("100px");
		cancelButton.setStyleName("okm-NoButton");
		cancelButton.addStyleName("btn");
		cancelButton.addStyleName("btn-warning");
		acceptButton.setStyleName("okm-YesButton");
		acceptButton.addStyleName("btn");
		acceptButton.addStyleName("btn-success");

		text.setHTML("");

		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(acceptButton);

		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));

		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * Execute the confirmed action
	 */
	private void execute() {
		switch (action) {
			case CONFIRM_DELETE_FORUM:
				controller.deleteForum(id);
				break;
			case CONFIRM_DELETE_POST:
				controller.deletePost(id);
				break;
		}

		action = NO_ACTION; // Resets action value
	}

	/**
	 * Sets the action to be confirmed
	 *
	 * @param action The action to be confirmed
	 */
	public void setConfirm(int action, long id, ForumController controller) {
		this.controller = controller;
		this.action = action;
		this.id = id;
		switch (action) {
			case CONFIRM_DELETE_FORUM:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.forum"));
				break;

			case CONFIRM_DELETE_POST:
				text.setHTML(GeneralComunicator.i18nExtension("confirm.delete.post"));
				break;
		}
	}

	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("confirm.label"));
		cancelButton.setHTML(OkmConstants.ICON_NO_BUTTON + GeneralComunicator.i18n("button.cancel"));
		acceptButton.setHTML(OkmConstants.ICON_YES_BUTTON + GeneralComunicator.i18n("button.accept"));
	}

	/**
	 * Shows de popup
	 */
	public void show() {
		setText(GeneralComunicator.i18n("confirm.label"));
		int left = (Window.getClientWidth() - 300) / 2;
		int top = (Window.getClientHeight() - 125) / 2;
		setPopupPosition(left, top);
		super.show();
	}
}