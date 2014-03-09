package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSearchCriteria;
import com.ziplly.app.model.AccountStatus;
import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessType;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PostalCodeDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.NeighborhoodSearchActionType;

public class AdminView extends Composite implements View<AdminView.AdminPresenter> {
	private static final int PAGE_SIZE = 10;

	private static AdminViewUiBinder uiBinder = GWT.create(AdminViewUiBinder.class);

	public static interface AdminPresenter extends Presenter {
		void searchTweets(int start, int end, TweetSearchCriteria tsc);

		void update(TweetDTO tweet);

		void update(AccountDTO account);

		void searchAccounts(int start, int end, AccountSearchCriteria asc);

		void updateAccount(AccountDTO a);

		void inviteForRegistration(String email, AccountType type, BusinessType btype);

		void searchNeighborhoods(GetNeighborhoodAction action);

		void updateNeighborhood(NeighborhoodDTO n);

		void createNeighborhood(NeighborhoodDTO n);

		void deleteNeighborhood(NeighborhoodDTO n);
	}

	interface AdminViewUiBinder extends UiBinder<Widget, AdminView> {
	}

	@UiField
	Alert message;

	@UiField
	TextBox zipCode;

	@UiField
	ListBox tweetCategoryListBox;

	@UiField
	ListBox tweetStatusListBox;

	@UiField
	Button tweetSearchBtn;

	@UiField
	Button accountSearchBtn;

	@UiField
	HTMLPanel tweetCellTablePanel;
	private SimplePager tweetsTablePager;
	private CellTable<TweetDTO> tweetsTable;

	@UiField
	HTMLPanel accountsCellTablePanel;
	private SimplePager accountsTablePager;
	private CellTable<AccountDTO> accountsTable;

	@UiField
	TextBox emailTextBox;
	@UiField
	ListBox accountTypeTextBox;
	@UiField
	TextBox nameTextBox;
	@UiField
	TextBox zipTextBox;

	@UiField
	NavLink accountSearchNavLink;
	@UiField
	HTMLPanel accountSearchPanel;

	@UiField
	NavLink registrationNavLink;
	@UiField
	HTMLPanel registrationPanel;
	@UiField
	ListBox businessTypeListBox;
	@UiField
	HTMLPanel businessTypePanel;

	@UiField
	TextBox emailInvitationTextBox;
	@UiField
	Button inviteBtn;

	@UiField
	RadioButton personalAccountType;
	@UiField
	RadioButton businessAccountType;

	// Neighborhood Editor
	@UiField
	HTMLPanel neighborhoodListPanel;
	CellTable<NeighborhoodDTO> neighborhoodTable;
	SimplePager neighborhoodTablePager;
	@UiField
	SubmitButton createNeighborhoodBtn;
	@UiField
	SubmitButton searchNeighborhoodBtn;
	@UiField
	FormPanel uploadForm;
	@UiField
	FileUpload uploadField;
	@UiField
	Image neighborhoodImagePreview;
	@UiField
	TextBox imageUrlTextField;
	
	private AdminPresenter presenter;
	
	private boolean imageUploaded;

	public AdminView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		displayAccountSearchPanel(true);
		setup();
		setupHandlers();
	}

	private void setupHandlers() {
		uploadField.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (imageUploaded) {
//					presenter.deleteImage(neighborhoodImagePreview.getUrl());
				}
				imageUploaded = true;
				uploadForm.submit();
			}
		});
	}

	private void setup() {
		for (TweetType type : TweetType.values()) {
			tweetCategoryListBox.addItem(type.name().toLowerCase());
		}

		for (TweetStatus status : TweetStatus.values()) {
			tweetStatusListBox.addItem(status.name().toLowerCase());
		}

		for (AccountType type : AccountType.values()) {
			accountTypeTextBox.addItem(type.name().toLowerCase());
		}

		for (BusinessType type : BusinessType.values()) {
			businessTypeListBox.addItem(type.name().toLowerCase());
		}

		buildAccountsTable();
		buildTweetsTable();
		buildNeighborhoodTable();
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
	}

	private void buildTweetsTable() {
		tweetsTable = new CellTable<TweetDTO>();
		Column<TweetDTO, Number> tweetIdCol = new Column<TweetDTO, Number>(new NumberCell()) {
			@Override
			public Long getValue(TweetDTO t) {
				if (t != null) {
					return t.getTweetId();
				}
				// TODO : shouldn't need this
				return 0L;
			}
		};
		tweetsTable.addColumn(tweetIdCol, "TweetId");
		tweetsTable.setColumnWidth(tweetIdCol, 5, Unit.PCT);

		Column<TweetDTO, String> tweetTypeCol = new Column<TweetDTO, String>(new TextCell()) {
			@Override
			public String getValue(TweetDTO t) {
				return t.getType().name().toLowerCase();
			}
		};
		tweetsTable.addColumn(tweetTypeCol, "Type");
		tweetsTable.setColumnWidth(tweetTypeCol, 5, Unit.PCT);

		Column<TweetDTO, String> tweetStatusCol = new Column<TweetDTO, String>(new TextCell()) {
			@Override
			public String getValue(TweetDTO t) {
				return t.getStatus().name().toLowerCase();
			}
		};
		tweetsTable.addColumn(tweetStatusCol, "Status");
		tweetsTable.setColumnWidth(tweetStatusCol, 5, Unit.PCT);

		Column<TweetDTO, String> tweetContentCol = new Column<TweetDTO, String>(new TextCell()) {
			@Override
			public String getValue(TweetDTO t) {
				return t.getContent();
			}
		};
		tweetsTable.addColumn(tweetContentCol, "Content");
		tweetsTable.setColumnWidth(tweetContentCol, 50, Unit.PCT);

		Column<TweetDTO, Date> timeCreated = new Column<TweetDTO, Date>(new DateCell()) {
			@Override
			public Date getValue(TweetDTO t) {
				return t.getTimeCreated();
			}
		};
		timeCreated.setSortable(true);
		tweetsTable.addColumn(timeCreated, "Time created");
		tweetsTable.setColumnWidth(timeCreated, 10, Unit.PCT);

		ActionCell<TweetDTO> deleteActionCell = new ActionCell<TweetDTO>("Delete",
				new ActionCell.Delegate<TweetDTO>() {

					@Override
					public void execute(TweetDTO t) {
						// Window.alert("Clicked on "+t.getTweetId());
						t.setStatus(TweetStatus.DELETED);
						presenter.update(t);
					}
				});

		Column<TweetDTO, TweetDTO> deleteActionCol = new Column<TweetDTO, TweetDTO>(
				deleteActionCell) {
			@Override
			public TweetDTO getValue(TweetDTO t) {
				return t;
			}
		};
		tweetsTable.addColumn(deleteActionCol);
		tweetsTable.setColumnWidth(deleteActionCol, 6, Unit.PCT);

		ActionCell<TweetDTO> activateActionCell = new ActionCell<TweetDTO>("Activate",
				new ActionCell.Delegate<TweetDTO>() {
					@Override
					public void execute(TweetDTO t) {
						t.setStatus(TweetStatus.ACTIVE);
						presenter.update(t);
					}
				});

		Column<TweetDTO, TweetDTO> activateActionCol = new Column<TweetDTO, TweetDTO>(
				activateActionCell) {
			@Override
			public TweetDTO getValue(TweetDTO t) {
				return t;
			}
		};
		tweetsTable.setColumnWidth(activateActionCol, 6, Unit.PCT);
		tweetsTable.addColumn(activateActionCol);

		// add pager
		tweetsTablePager = new SimplePager();
		tweetsTablePager.setDisplay(tweetsTable);
		tweetsTablePager.setPageSize(PAGE_SIZE);

		// add the table
		tweetCellTablePanel.add(tweetsTablePager);
		tweetCellTablePanel.add(tweetsTable);

		// setup handlers
		tweetsTable.addRangeChangeHandler(new RangeChangeHandler());
	}

	private void buildAccountsTable() {
		accountsTable = new CellTable<AccountDTO>();
		Column<AccountDTO, Number> accountIdCol = new Column<AccountDTO, Number>(new NumberCell()) {
			@Override
			public Long getValue(AccountDTO a) {
				if (a != null) {
					return a.getAccountId();
				}
				// TODO ???
				return 0L;
			}
		};
		accountsTable.addColumn(accountIdCol, "AccountId");
		accountsTable.setColumnWidth(accountIdCol, 5, Unit.PCT);

		Column<AccountDTO, String> nameCol = new Column<AccountDTO, String>(new TextCell()) {
			@Override
			public String getValue(AccountDTO a) {
				return a.getDisplayName();
			}
		};
		accountsTable.addColumn(nameCol, "Name");
		accountsTable.setColumnWidth(nameCol, 12, Unit.PCT);

		Column<AccountDTO, String> emailCol = new Column<AccountDTO, String>(new TextCell()) {
			@Override
			public String getValue(AccountDTO a) {
				return a.getEmail();
			}
		};
		accountsTable.addColumn(emailCol, "Email");
		accountsTable.setColumnWidth(emailCol, 12, Unit.PCT);

		Column<AccountDTO, String> accountTypeCol = new Column<AccountDTO, String>(new TextCell()) {
			@Override
			public String getValue(AccountDTO a) {
				if (a instanceof PersonalAccountDTO) {
					return "personal";
				} else if (a instanceof BusinessAccountDTO) {
					return "business";
				}
				return "unknown";
			}
		};
		accountsTable.addColumn(accountTypeCol, "Type");
		accountsTable.setColumnWidth(accountTypeCol, 5, Unit.PCT);

		Column<AccountDTO, String> statusCol = new Column<AccountDTO, String>(new TextCell()) {
			@Override
			public String getValue(AccountDTO a) {
				if (a != null) {
					if (a.getStatus() != null) {
						return a.getStatus().toString();
					}
				}
				return "";
			}
		};
		accountsTable.addColumn(statusCol, "status");
		accountsTable.setColumnWidth(statusCol, 8, Unit.PCT);

		Column<AccountDTO, Date> timeCreatedCol = new Column<AccountDTO, Date>(new DateCell()) {
			@Override
			public Date getValue(AccountDTO a) {
				return a.getTimeCreated();
			}
		};
		accountsTable.addColumn(timeCreatedCol, "Time created");
		accountsTable.setColumnWidth(timeCreatedCol, 8, Unit.PCT);

		// action
		ActionCell<AccountDTO> activateActionCell = new ActionCell<AccountDTO>("activate",
				new ActionCell.Delegate<AccountDTO>() {
					@Override
					public void execute(AccountDTO a) {
						a.setStatus(AccountStatus.ACTIVE);
						presenter.updateAccount(a);
					}
				});
		Column<AccountDTO, AccountDTO> activateAccountCol = new Column<AccountDTO, AccountDTO>(
				activateActionCell) {
			@Override
			public AccountDTO getValue(AccountDTO acct) {
				return acct;
			}
		};
		accountsTable.addColumn(activateAccountCol);

		ActionCell<AccountDTO> deactivateActionCell = new ActionCell<AccountDTO>("deactivate",
				new ActionCell.Delegate<AccountDTO>() {
					@Override
					public void execute(AccountDTO a) {
						a.setStatus(AccountStatus.SUSPENDED);
						presenter.updateAccount(a);
					}
				});
		Column<AccountDTO, AccountDTO> deactivateAccountCol = new Column<AccountDTO, AccountDTO>(
				deactivateActionCell) {
			@Override
			public AccountDTO getValue(AccountDTO acct) {
				return acct;
			}
		};
		accountsTable.addColumn(deactivateAccountCol);

		// add pager
		accountsTablePager = new SimplePager();
		accountsTablePager.setDisplay(accountsTable);
		accountsTablePager.setPageSize(PAGE_SIZE);

		// add the table
		accountsCellTablePanel.add(accountsTablePager);
		accountsCellTablePanel.add(accountsTable);

		// setup handlers
		accountsTable.addRangeChangeHandler(new AccountsTableRangeChangeHandler());
	}

	private void buildNeighborhoodTable() {
		neighborhoodTable = new CellTable<NeighborhoodDTO>();

		Column<NeighborhoodDTO, Number> idColumn = new Column<NeighborhoodDTO, Number>(
				new NumberCell()) {

			@Override
			public Long getValue(NeighborhoodDTO n) {
				return n.getNeighborhoodId();
			}
		};
		neighborhoodTable.addColumn(idColumn, "Id");

		Column<NeighborhoodDTO, String> nameColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				return n.getName();
			}
		};
		neighborhoodTable.addColumn(nameColumn, "Name");
		nameColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO n, String value) {
				n.setName(value);
			}
		});
		
		Column<NeighborhoodDTO, String> cityColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				return n.getCity();
			}
		};
		neighborhoodTable.addColumn(cityColumn, "City");
		cityColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO object, String value) {
				if (value == null) {
					Window.alert("City can't be null");
					return;
				}
				object.setCity(value);
			}
			
		});
		
		Column<NeighborhoodDTO, String> stateColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				return n.getState();
			}
		};
		neighborhoodTable.addColumn(stateColumn, "State");
		stateColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO object, String value) {
				if (value == null) {
					Window.alert("State can't be null");
					return;
				}
				object.setState(value);
			}
		});
		
		Column<NeighborhoodDTO, String> parentNeighborhoodColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				if (n.getParentNeighborhood() != null) {
					return n.getParentNeighborhood().getNeighborhoodId().toString();
				}
				return "";
			}
		};
		neighborhoodTable.addColumn(parentNeighborhoodColumn, "Parent Neighborhood Id");
		parentNeighborhoodColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO object, String value) {
				NeighborhoodDTO parent = new NeighborhoodDTO();
				parent.setNeighborhoodId(Long.parseLong(value));
				object.setParentNeighborhood(parent);
			}
		});
		
		Column<NeighborhoodDTO, String> zipColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				String v = "";
				for(PostalCodeDTO p : n.getPostalCodes()) {
					v += p.getPostalCode()+",";
				}
				return v;
			}
		};
		neighborhoodTable.addColumn(zipColumn, "Zip code");
		zipColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO object, String value) {
				try {
					Long.parseLong(value);
				} catch(NumberFormatException ex) {
					Window.alert("Invalid parent id");
					return;
				}
				PostalCodeDTO p = new PostalCodeDTO();
				p.setPostalCode(value);
				object.addPostalCode(p);
			}
		});
		
		Column<NeighborhoodDTO, String> neighborhoodImageUrlColumn = new Column<NeighborhoodDTO, String>(
				new EditTextCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				if (n.getImageUrl() != null) {
					return n.getImageUrl();
				}
				return "";
			}
		};
		neighborhoodTable.addColumn(neighborhoodImageUrlColumn, "Image url");
		neighborhoodImageUrlColumn.setFieldUpdater(new FieldUpdater<NeighborhoodDTO, String>() {

			@Override
			public void update(int index, NeighborhoodDTO object, String value) {
				if (imageDto == null) {
					throw new RuntimeException("Image shouldn't be null");
				}
				
				object.addImage(imageDto);
				object.setImageUrl(value);
			}
		});
		
		Column<NeighborhoodDTO, String> neighborhoodImageColumn = new Column<NeighborhoodDTO, String>(
				new ImageCell()) {

			@Override
			public String getValue(NeighborhoodDTO n) {
				return n.getImageUrl();

			}
		};
		neighborhoodTable.addColumn(neighborhoodImageColumn, "Image");
		
		ActionCell<NeighborhoodDTO> updateButtonCell = new ActionCell<NeighborhoodDTO>("Update",
				new ActionCell.Delegate<NeighborhoodDTO>() {
					@Override
					public void execute(NeighborhoodDTO n) {
						// WARNING (P1)
						//
						// It's going to attach the uploaded image to the neighborhood
						// being updated. NEEDS TO CHANGE SOON!!!
						//
						//
						if (imageDto != null) {
							n.addImage(imageDto);
						}
						presenter.updateNeighborhood(n);
					}
				});
		Column<NeighborhoodDTO, NeighborhoodDTO> saveColumn = new Column<NeighborhoodDTO, NeighborhoodDTO>(updateButtonCell) {
			@Override
			public NeighborhoodDTO getValue(NeighborhoodDTO o) {
				return o;
			}
		};
		neighborhoodTable.addColumn(saveColumn);
		
		ActionCell<NeighborhoodDTO> deleteButtonCell = new ActionCell<NeighborhoodDTO>("Delete",
				new ActionCell.Delegate<NeighborhoodDTO>() {
					@Override
					public void execute(NeighborhoodDTO n) {
						presenter.deleteNeighborhood(n);
					}
				});
		Column<NeighborhoodDTO, NeighborhoodDTO> deleteColumn = new Column<NeighborhoodDTO, NeighborhoodDTO>(deleteButtonCell) {
			@Override
			public NeighborhoodDTO getValue(NeighborhoodDTO o) {
				return o;
			}
		};
		neighborhoodTable.addColumn(deleteColumn);
		
		// add pager
		neighborhoodTablePager = new SimplePager();
		neighborhoodTablePager.setPage(PAGE_SIZE);
		neighborhoodTablePager.setDisplay(neighborhoodTable);

		// add table
		neighborhoodListPanel.add(neighborhoodTable);
		neighborhoodListPanel.add(neighborhoodTablePager);

		neighborhoodTable.addRangeChangeHandler(new NeighborhoodTableRangeChangeHandler());
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	public void clear() {
		message.setVisible(false);
	}

	@UiHandler("tweetSearchBtn")
	public void searchTweet(ClickEvent event) {
		refresh();
	}

	@UiHandler("accountSearchBtn")
	public void searchAccount(ClickEvent event) {
		refreshAccount();
	}

	private void refreshAccount() {
		AccountSearchCriteria asc = getAccountSearchCriteria();
		presenter.searchAccounts(0, PAGE_SIZE, asc);
	}

	private AccountSearchCriteria getAccountSearchCriteria() {
		AccountSearchCriteria asc = new AccountSearchCriteria();
		if (nameTextBox.getText() != null) {
			asc.setName(FieldVerifier.sanitize(nameTextBox.getText()));
		}

		if (zipTextBox.getText() != null && !"".equals(zipTextBox.getText())) {
			asc.setZipCode(Integer.parseInt(FieldVerifier.sanitize(zipTextBox.getText())));
		}

		if (emailTextBox.getText() != null) {
			asc.setEmail(FieldVerifier.sanitize(emailTextBox.getText()));
		}

		AccountType accountType = AccountType.values()[accountTypeTextBox.getSelectedIndex()];
		asc.setType(accountType);
		return asc;
	}

	private TweetSearchCriteria getTweetSearchCriteria() {
		TweetSearchCriteria tsc = new TweetSearchCriteria();
		if (zipCode.getText() != null) {
			try {
				int zip = Integer.parseInt(zipCode.getText());
				tsc.setZip(zip);
			} catch (NumberFormatException nfe) {
				// swallow exception and leave zip as null
			}
		}

		for (int i = 0; i < tweetCategoryListBox.getItemCount(); i++) {
			boolean itemSelected = tweetCategoryListBox.isItemSelected(i);
			if (itemSelected) {
				TweetType type = TweetType.valueOf(tweetCategoryListBox.getItemText(i)
						.toUpperCase());
				tsc.addType(type);
			}
		}

		TweetStatus status = TweetStatus.values()[tweetStatusListBox.getSelectedIndex()];
		tsc.setStatus(status);
		return tsc;
	}

	public void refresh() {
		TweetSearchCriteria tsc = getTweetSearchCriteria();
		presenter.searchTweets(0, PAGE_SIZE, tsc);
	}

	public void setTweetRowCount(int count) {
		tweetsTable.setRowCount(count, true);
	}

	public void setTweetData(int start, List<TweetDTO> tweets) {
		tweetsTable.setRowData(start, tweets);
	}

	public void setAccountData(int start, List<AccountDTO> accounts) {
		accountsTable.setRowData(start, accounts);
	}

	public void setAccountRowCount(int count) {
		accountsTable.setRowCount(count);
	}

	private class RangeChangeHandler implements RangeChangeEvent.Handler {
		@Override
		public void onRangeChange(RangeChangeEvent event) {
			Range range = event.getNewRange();
			int start = range.getStart();
			int end = start + range.getLength();
			TweetSearchCriteria tweetSearchCriteria = getTweetSearchCriteria();
			// call presenter;
			presenter.searchTweets(start, end, tweetSearchCriteria);
		}
	}

	private class AccountsTableRangeChangeHandler implements RangeChangeEvent.Handler {
		@Override
		public void onRangeChange(RangeChangeEvent event) {
			Range range = event.getNewRange();
			int start = range.getStart();
			int end = start + range.getLength();
			AccountSearchCriteria asc = getAccountSearchCriteria();
			// call presenter;
			presenter.searchAccounts(start, end, asc);
		}
	}

	private class NeighborhoodTableRangeChangeHandler implements RangeChangeEvent.Handler {

		@Override
		public void onRangeChange(RangeChangeEvent event) {
			Range range = event.getNewRange();
			int start = range.getStart();
			int end = start + range.getLength();
			GetNeighborhoodAction action = new GetNeighborhoodAction();
			action.setSearchType(NeighborhoodSearchActionType.ALL);
			presenter.searchNeighborhoods(action);
		}
	}

	@Override
	public void setPresenter(AdminView.AdminPresenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("accountSearchNavLink")
	void searchAccountLinkClicked(ClickEvent event) {
		displayAccountSearchPanel(true);
		displayAccountRegistrationPanel(false);
	}

	private void displayAccountSearchPanel(boolean display) {
		if (display) {
			accountSearchPanel.getElement().getStyle().setDisplay(Display.BLOCK);
			accountSearchNavLink.setActive(true);
			displayAccountRegistrationPanel(false);
		} else {
			accountSearchPanel.getElement().getStyle().setDisplay(Display.NONE);
			accountSearchNavLink.setActive(false);
			displayAccountRegistrationPanel(true);
		}
	}

	@UiHandler("registrationNavLink")
	void registerAccountLinkClicked(ClickEvent event) {
		displayAccountSearchPanel(false);
		displayAccountRegistrationPanel(true);
	}

	private void displayAccountRegistrationPanel(boolean display) {
		if (display) {
			registrationPanel.getElement().getStyle().setDisplay(Display.BLOCK);
			registrationNavLink.setActive(true);
		} else {
			registrationPanel.getElement().getStyle().setDisplay(Display.NONE);
			registrationNavLink.setActive(false);
		}
	}

	@UiHandler("inviteBtn")
	void inviteForRegistration(ClickEvent event) {
		String email = emailInvitationTextBox.getText();
		AccountType type = personalAccountType.isEnabled() ? AccountType.PERSONAL
				: AccountType.BUSINESS;
		BusinessType btype = BusinessType.values()[businessTypeListBox.getSelectedIndex()];
		presenter.inviteForRegistration(email, type, btype);
	}

	public void setNeighborhoodData(int start, List<NeighborhoodDTO> neighbordhoods) {
		neighborhoodTable.setRowData(start, neighbordhoods);
	}

	@UiHandler("searchNeighborhoodBtn")
	public void searchNeigjborhood(ClickEvent event) {
		GetNeighborhoodAction action = new GetNeighborhoodAction();
		action.setSearchType(NeighborhoodSearchActionType.ALL);
		presenter.searchNeighborhoods(action);
	}

	@UiField
	TextBox neighborhoodZip;
	@UiField
	TextBox neighborhoodCity;
	@UiField
	TextBox neighborhoodState;
	@UiField
	TextBox neighborhoodName;
	@UiField
	TextBox parentNeighborhoodId;
	
	@UiHandler("createNeighborhoodBtn")
	public void createNeighborhood(ClickEvent event) {
		NeighborhoodDTO n = new NeighborhoodDTO();
		n.setCity(neighborhoodCity.getText());
		n.setState(neighborhoodState.getText());
		n.setName(neighborhoodName.getText());
		
		if (!"".equals(parentNeighborhoodId.getText())) {
			NeighborhoodDTO parent = new NeighborhoodDTO();
			parent.setNeighborhoodId(Long.parseLong(parentNeighborhoodId.getText()));
			n.setParentNeighborhood(parent);
		}	
		
		PostalCodeDTO p = new PostalCodeDTO();
		p.setPostalCode(neighborhoodZip.getText());
		n.addPostalCode(p);
		presenter.createNeighborhood(n);
	}
	
	public void setUploadFormActionUrl(String imageUrl) {
		uploadForm.setAction(imageUrl);
	}

	public void setUploadFormSubmitCompleteHandler(SubmitCompleteHandler handler) {
		uploadForm.addSubmitCompleteHandler(handler);
	}

	private ImageDTO imageDto;
	
	public void displayImagePreview(String imageUrl) {
		imageDto = ImageUtil.parseImageUrl(imageUrl);
		neighborhoodImagePreview.setUrl(imageDto.getUrl());
		imageUrlTextField.setText(imageUrl);
		imageUploaded = true;
	}

	public void resetUploadForm() {
		uploadForm.reset();
	}

	public void resetNeighborhoodImageUploadForm() {
		uploadForm.setAction("");
	}
}
