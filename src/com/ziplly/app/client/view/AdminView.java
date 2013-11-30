package com.ziplly.app.client.view;

import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSearchCriteria;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;
import com.ziplly.app.model.TweetStatus;
import com.ziplly.app.model.TweetType;

public class AdminView extends Composite implements View<AdminView.AdminPresenter>{
	private static final int PAGE_SIZE = 10;

	private static AdminViewUiBinder uiBinder = GWT.create(AdminViewUiBinder.class);

	public static interface AdminPresenter extends Presenter {
		void searchTweets(int start, int end, TweetSearchCriteria tsc);
		void update(TweetDTO tweet);
		
		void update(AccountDTO account);
		void searchAccounts(int start, int end, AccountSearchCriteria asc);
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
	
	private AdminPresenter presenter;
	
	public AdminView() {
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
		setup();
	}

	private void setup() {
		for (TweetType type : TweetType.values()) {
			tweetCategoryListBox.addItem(type.name().toLowerCase());
		}

		for (TweetStatus status : TweetStatus.values()) {
			tweetStatusListBox.addItem(status.name().toLowerCase());
		}
	
		buildAccountsTable();
		buildTweetsTable();
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
//						Window.alert("Clicked on "+t.getTweetId());
						t.setStatus(TweetStatus.DELETED);
						presenter.update(t);
					}
		});
		
		Column<TweetDTO, TweetDTO> deleteActionCol = new Column<TweetDTO, TweetDTO>(deleteActionCell) {
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
		Column<TweetDTO, TweetDTO> activateActionCol = new Column<TweetDTO, TweetDTO>(activateActionCell) {

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
				return a.getAccountId();
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
		accountsTable.addColumn(nameCol, "Email");
		accountsTable.setColumnWidth(nameCol, 12, Unit.PCT);
		
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
				if (a.getState() != null) {
					return a.getState().toLowerCase();
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

		// add pager
		accountsTablePager = new SimplePager();
		accountsTablePager.setDisplay(accountsTable);
		accountsTablePager.setPageSize(PAGE_SIZE);
		
		// add the table
		accountsCellTablePanel.add(accountsTablePager);
		accountsCellTablePanel.add(accountsTable);
		
		// setup handlers
//		accountsTable.addRangeChangeHandler(new RangeChangeHandler());
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
	
	public void setTweetData(int start, List<TweetDTO> tweets){
		tweetsTable.setRowData(start, tweets);
	}
	
	public void setAccountData(List<AccountDTO> accounts) {
		accountsTable.setRowData(accounts);
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

	@Override
	public void setPresenter(AdminView.AdminPresenter presenter) {
		this.presenter = presenter;
	}
}
