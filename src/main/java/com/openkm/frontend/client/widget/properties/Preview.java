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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.constants.service.RPCService;
import com.openkm.frontend.client.extension.widget.preview.PreviewExtension;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;

/**
 * Notes
 *
 * @author jllort
 */
public class Preview extends Composite {
	private static final int TURN_BACK_HEIGHT = 25;
	private VerticalPanel vPanel;
	private VerticalPanel vPanelOut;
	private HTML pdf;
	private HTML swf;
	private HTML video;
	public HTMLPreview htmlPreview;
	public SyntaxHighlighterPreview syntaxHighlighterPreview;
	private int width = 0;
	private int height = 0;
	private boolean previewAvailable = false;
	private boolean previewConversion = true;
	String mediaUrl = "";
	private String mediaProvider = "";
	private List<PreviewExtension> widgetPreviewExtensionList;
	private HasPreviewEvent previewEvent;
	private HorizontalPanel hReturnPanel;
	private Button backButton;
	private String pdfID = "jsPdfViewer";
	public EmbeddedPreview embeddedPreview;
	private String pdfContainer = "pdfembededcontainer";

	private HorizontalPanel hPanelMustReads;
	private HTML mustReadImage;
	private HTML mustReadImageDisable;
	private HTML mustReadText;

	/**
	 * Preview
	 */
	public Preview(final HasPreviewEvent previewEvent) {
		this.previewEvent = previewEvent;
		widgetPreviewExtensionList = new ArrayList<PreviewExtension>();
		vPanel = new VerticalPanel();
		htmlPreview = new HTMLPreview();
		syntaxHighlighterPreview = new SyntaxHighlighterPreview();
		pdf = new HTML("<div id=\"pdfembededcontainer\"></div>\n");
		swf = new HTML("<div id=\"pdfviewercontainer\"></div>\n");
		video = new HTML("<div id=\"mediaplayercontainer\"></div>\n");
		hReturnPanel = new HorizontalPanel();
		hReturnPanel.setWidth("100%");
		backButton = new Button(Main.i18n("search.button.preview.back"));
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				previewEvent.returnBack();
			}
		});
		backButton.setStylePrimaryName("okm-Button");
		HTML space2 = Util.hSpace("5px");
		hReturnPanel.add(space2);
		hReturnPanel.add(backButton);
		hReturnPanel.setCellWidth(space2, "5px");
		hReturnPanel.setCellHorizontalAlignment(backButton, HasAlignment.ALIGN_LEFT);
		hReturnPanel.setCellVerticalAlignment(backButton, HasAlignment.ALIGN_MIDDLE);
		hReturnPanel.setHeight(String.valueOf(TURN_BACK_HEIGHT) + "px");
		hReturnPanel.setStyleName("okm-TopPanel");
		hReturnPanel.addStyleName("okm-Border-Top");
		hReturnPanel.addStyleName("okm-Border-Left");
		hReturnPanel.addStyleName("okm-Border-Right");
		embeddedPreview = new EmbeddedPreview();

		//for must read check
		hPanelMustReads = new HorizontalPanel();
		mustReadText = new HTML("<br/><b> X??c nh???n ?????c v?? hi???u ?</b>");
		hPanelMustReads.add(mustReadText);
		hPanelMustReads.add(new HTML("&nbsp;"));
		mustReadImage = new HTML("<br/><span class=\"glyphicons glyphicons-ok-circle child-menuitem-glyphicon-renew\" style=\"color: red; font-size: 25px\"></span>");
		mustReadImage.addStyleName("okm-Hyperlink");

//		mustReadImageDisable = new HTML("<br/><span class=\"glyphicons glyphicons-ok-circle child-menuitem-glyphicon-renew disable-Confirm-Button\" style=\"color: red\"></span>");
		mustReadImageDisable = new HTML("<br/><span class=\"glyphicons glyphicons-ok-circle child-menuitem-glyphicon-renew\" style=\"color: green; font-size: 25px\"></span>");
		mustReadImageDisable.addStyleName("disable-Confirm");
		mustReadImageDisable.setVisible(false);
		mustReadImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeReadDoc();
			}
		});
		hPanelMustReads.add(mustReadImage);
		hPanelMustReads.add(mustReadImageDisable);
		hPanelMustReads.setCellVerticalAlignment(mustReadText, HasAlignment.ALIGN_MIDDLE);
		vPanelOut = new VerticalPanel();
		vPanelOut.add(hPanelMustReads);
		vPanelOut.setCellHorizontalAlignment(hPanelMustReads, HorizontalPanel.ALIGN_CENTER);
		vPanelOut.add(vPanel);
		initWidget(vPanelOut);
	}

	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		this.width = (previewEvent == null) ? width : width;
		this.height = (previewEvent == null) ? height : height - TURN_BACK_HEIGHT;
		htmlPreview.setPixelSize(this.width, this.height);
		syntaxHighlighterPreview.setPixelSize(this.width, this.height);
		embeddedPreview.setPixelSize(this.width, this.height);
	}

	/**
	 * showHTML
	 */
	public void showHTML(GWTDocument doc) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(htmlPreview);
		vPanel.setCellHorizontalAlignment(htmlPreview, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(htmlPreview, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			htmlPreview.showHTML(doc);
		}
	}

	/**
	 * showSyntaxHighlighterHTML
	 */
	public void showSyntaxHighlighterHTML(GWTDocument doc) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(syntaxHighlighterPreview);
		vPanel.setCellHorizontalAlignment(syntaxHighlighterPreview, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(syntaxHighlighterPreview, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			syntaxHighlighterPreview.showHightlighterHTML(doc);
		}
	}

	/**
	 * showEmbedSWF
	 *
	 * @param uuid Unique document ID to be previewed.
	 */
	public void showEmbedSWF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(swf);
		vPanel.setCellHorizontalAlignment(swf, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(swf, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			if (previewConversion) {
				String url = RPCService.ConverterServlet + "?inline=true&toSwf=true&uuid=" + URL.encodeQueryString(uuid)
						+"&userId="+Main.get().workspaceUserProperties.getUser().getId();
				swf.setHTML("<div id=\"pdfviewercontainer\"></div>\n"); // needed for rewriting purpose

				if (Main.get().workspaceUserProperties.getWorkspace().getPreviewer().equals("flexpaper")) {
					if (Main.get().workspaceUserProperties.getWorkspace().isPrintPreview()) {
						Util.createPDFViewerFlexPaper(url, "" + width, "" + height);
					} else {
						Util.createPDFViewerFlexPaper(url, "" + width, "" + height);
					}
				}

				Main.get().conversionStatus.getStatus();
			} else {
				String url = RPCService.DownloadServlet + "?inline=true&uuid=" + URL.encodeQueryString(uuid);
				swf.setHTML("<div id=\"swfviewercontainer\"></div>\n"); // needed for rewriting purpose
				Util.createSwfViewer(url, "" + width, "" + height);
			}
		} else {
			swf.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable")
					+ "</div>\n"); // needed for rewriting purpose
		}
	}

	/**
	 * resizeEmbedSWF
	 */
	public void resizeEmbedSWF(int width, int height) {
		if (previewConversion) {
			if (Main.get().workspaceUserProperties.getWorkspace().getPreviewer().equals("flexpaper")) {
				Util.resizePDFViewerFlexPaper("" + width, "" + height);
			}
		} else {
			Util.resizeSwfViewer("" + width, "" + height);
		}
	}

	/**
	 * showEmbedPDF
	 *
	 * @param uuid Unique document ID to be previewed.
	 */
	public void showEmbedPDF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(pdf);
		vPanel.setCellHorizontalAlignment(pdf, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(pdf, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			String url = RPCService.DownloadServlet + "?inline=true&uuid=" + URL.encodeQueryString(uuid);
			pdf.setHTML("<div id=\"pdfembededcontainer\">" +
					"<object id=\"" + pdfID + "\" name=\"" + pdfID + "\" width=\"" + width + "\" height=\"" + height + "\" type=\"application/pdf\" data=\"" + url + "\"&#zoom=85&scrollbar=1&toolbar=1&navpanes=1&view=FitH\">" +
					"<p>Browser plugin suppport error, PDF can not be displayed</p>" +
					"</object>" +
					"</div>\n"); // needed for rewriting  purpose
		} else {
			swf.setHTML("<div id=\"pdfembededcontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n");
		}
	}

	/**
	 * cleanPreview
	 */
	public void cleanPreview() {
		swf.setHTML("<div id=\"pdfviewercontainer\" ></div>\n");
		embeddedPreview.clear();
	}

	/**
	 * Set the media file to reproduce
	 *
	 * @param mediaUrl The media file url
	 */
	public void showMediaFile(String mediaUrl, String mimeType, String docUuid) {
		hideWidgetExtension();
		vPanel.clear();


		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(video);
		vPanel.setCellHorizontalAlignment(video, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(video, HasAlignment.ALIGN_MIDDLE);

		this.mediaUrl = mediaUrl;
		Util.removeMediaPlayer();
		video.setHTML(
				"<div id=\"mediaplayercontainer\">" +
						"<video width=\"854\" height=\"480\" controls >\n" +
						"    <source src=\"/kms/services/rest/document/video?uuid=" + docUuid + "&userId=" +Main.get().workspaceUserProperties.getUser().getId()+"\""+
						" type=\"video/mp4\">\n" +
						"</video>" +
				"</div>\n"
		);

/*		if (mimeType.equals("audio/mpeg")) {
			mediaProvider = "sound";
		} else if (mimeType.equals("video/x-flv") || mimeType.equals("video/mp4")) {
			mediaProvider = "video";
		} else if (mimeType.equals("application/x-shockwave-flash")) {
			mediaProvider = "";
		}


		Util.createMediaPlayer(mediaUrl, mediaProvider, "" + width, "" + height);*/
	}

	/**
	 * resizeMediaPlayer
	 */
	public void resizeMediaPlayer(int width, int height) {
		Util.resizeMediaPlayer("" + width, "" + height);
	}

	/**
	 * setPreviewExtension
	 */
	public void showPreviewExtension(PreviewExtension preview, String url) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		if (previewAvailable) {
			vPanel.add(preview.getWidget());
			preview.createViewer(url, width, height);
		}
	}

	/**
	 * hideWidgetExtension
	 */
	private void hideWidgetExtension() {
		if (vPanel.getWidgetCount() > 4) {
			for (int i = 3; i < vPanel.getWidgetCount(); i++) {
				vPanel.getWidget(i).setVisible(false);
			}
		}
	}

	/**
	 * Sets the boolean value if previewing document is available
	 *
	 * @param previewAvailable Set preview availability status.
	 */
	public void setPreviewAvailable(boolean previewAvailable) {
		this.previewAvailable = previewAvailable;
	}

	/**
	 * Sets the boolean value if document preview does not need conversion
	 */
	public void setPreviewConversion(boolean previewConversion) {
		this.previewConversion = previewConversion;
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		if (!previewAvailable) {
			swf.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable")
					+ "</div>\n"); // needed for rewriting purpose
		}
		backButton.setHTML(Main.i18n("search.button.preview.back"));
	}

	/**
	 * previewDocument
	 */
	public void previewDocument(boolean refreshing, GWTDocument doc) {
		GWT.log("MIME: " + doc.getMimeType());
		Main.get().mainPanel.dashboard.userDashboard.startReadDoc(Main.get().workspaceUserProperties.getUser().getId(), doc.getUuid());
		Main.get().mainPanel.dashboard.userDashboard.getMustReadDocuments();
		if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || doc.getMimeType().equals("audio/mpeg")) {
			if (!refreshing) {
				showMediaFile(RPCService.DownloadServlet + "?uuid=" + URL.encodeQueryString(doc.getUuid()), doc.getMimeType(), doc.getUuid());
//			showHTML(doc);
			} else {
				resizeMediaPlayer(width, height);
			}
		}
//		else if (HTMLPreview.isPreviewAvailable(doc.getMimeType())) {
//			if (!refreshing) {
//				showHTML(doc);
//			}
//		}
		else if (SyntaxHighlighterPreview.isPreviewAvailable(doc.getMimeType())) {
			if (!refreshing) {
				showSyntaxHighlighterHTML(doc);
			}
		} else if (doc.getMimeType().equals("application/pdf")) {
			setPreviewConversion(false);
			if (!refreshing) {
				showPDF(doc.getUuid());
			}
		} else if (doc.getMimeType().equals("application/x-shockwave-flash")) {
			setPreviewConversion(false);

			if (!refreshing) {
				showEmbedSWF(doc.getUuid());
			} else {
				resizeEmbedSWF(width, height);
			}
		} else {
			if (Main.get().workspaceUserProperties.getWorkspace().isAcrobatPluginPreview() && doc.getMimeType().equals("application/pdf")) {
				if (!refreshing) {
					showEmbedPDF(doc.getUuid());
				} else {
					Util.resizeEmbededPDF("" + width, "" + height, pdfID);
				}
			} else {
				setPreviewConversion(true);

				if (!refreshing) {
					if (doc.isConvertibleToPdf()) {
						showPDF(doc.getUuid());
					} else {
						showEmbedSWF(doc.getUuid());
					}
				} else {
					if (!doc.isConvertibleToPdf()) {
						resizeEmbedSWF(width, height);
					}
				}
			}
		}
	}

	/**
	 * addPreviewExtension
	 */
	public void addPreviewExtension(PreviewExtension extension) {
		widgetPreviewExtensionList.add(extension);
	}

	/**
	 * Preview PDF, take in consideration profile selection
	 */
	public void showPDF(String uuid) {
		hideWidgetExtension();
		vPanel.clear();

		vPanel.add(pdf);
		vPanel.setCellHorizontalAlignment(pdf, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(pdf, HasAlignment.ALIGN_MIDDLE);

		if (previewAvailable) {
			pdf.setHTML("<div id=\"" + pdfContainer + "\"></div>\n"); // needed for rewriting  purpose
			showSystemEmbeddedPreview(EmbeddedPreview.PDFJS_URL + URL.encodeQueryString(RPCService.ConverterServlet + "?toPdf=true&inline=true&uuid=" + uuid
			+"&userId="+Main.get().workspaceUserProperties.getUser().getId()));
		} else {
			pdf.setHTML("<div id=\"" + pdfContainer + "\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n");
		}
	}

	/**
	 * showSystemEmbeddedPreview
	 */
	public void showSystemEmbeddedPreview(String url) {
		hideWidgetExtension();
		vPanel.clear();

		if (previewEvent != null) {
			vPanel.add(hReturnPanel);
			vPanel.setCellHeight(hReturnPanel, String.valueOf(TURN_BACK_HEIGHT) + "px");
		}

		vPanel.add(embeddedPreview);
		vPanel.setCellHorizontalAlignment(embeddedPreview, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(embeddedPreview, HasAlignment.ALIGN_MIDDLE);

		embeddedPreview.showEmbedded(url);
	}

	private void executeReadDoc() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_READ_DOC);
		Main.get().confirmPopup.center();
	}

	public void setMustReadIconVisiable(boolean visible){
		mustReadImage.setVisible(visible);
		mustReadImageDisable.setVisible(!visible);
	}
	public void setShowMustReadIconVisiable(boolean visible){
		hPanelMustReads.setVisible(visible);
	}
}
