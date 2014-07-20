package com.ziplly.app.client.view.community;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.activities.util.CommonUtil;
import com.ziplly.app.client.places.AttributeKey;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.cell.PersonalAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetEntityListAction;

public class ResidentsView extends AbstractView implements
    View<ResidentsView.EntityListViewPresenter> {

	public interface EntityListViewPresenter extends Presenter {
		public void getPersonalAccountList(GetEntityListAction currentEntityListAction);
	}

	private static ResidentsViewUiBinder uiBinder = GWT.create(ResidentsViewUiBinder.class);

	interface ResidentsViewUiBinder extends UiBinder<Widget, ResidentsView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<PersonalAccountDTO> residentList;

	@UiField
	ListBox genderListBox;

	@UiField
	ListBox neighborhoodListBox;

	@UiField
	Button searchBtn;

	@UiField
	Button resetBtn;

	@UiField
	Alert status;

	@UiField
	Alert message;

	private EntityListViewPresenter presenter;
	private SendMessageWidget smw;
	private ResidentViewState state;
	private Map<String, Gender> genderList = new LinkedHashMap<String, Gender>();

	private List<NeighborhoodDTO> neighborhoods;

	@Inject
	public ResidentsView(EventBus eventBus) {
		super(eventBus);
		ResidentPlace place = getPlaceFromUrl();
		state = new ResidentViewState(EntityType.PERSONAL_ACCOUNT, place.getNeighborhoodId(), place.getGender());
		residentList = new CellList<PersonalAccountDTO>(new PersonalAccountCell(state));
		residentList.setPageSize(state.getPageSize());
		pager = new SimplePager();
		pager.setDisplay(residentList);
		initWidget(uiBinder.createAndBindUi(this));
		message.setAnimation(true);
		message.setClose(false);
		StyleHelper.show(status.getElement(), false);
		status.setClose(false);
		renderGenderListBox();
		setupHandlers();
	}

	 private void renderGenderListBox() {
	    genderList.clear();
	    genderListBox.clear();
	    for (Gender g : Gender.getValuesForSearch()) {
	      String genderName = basicDataFormatter.format(g, ValueType.GENDER);
	      genderListBox.addItem(genderName);
	      genderList.put(genderName, g);
	    }
	    
	    setSelectedGender(state.getGender());
  }

  private ResidentPlace getPlaceFromUrl() {
	    ResidentPlace place = new ResidentPlace();
	    try {
	      String neighborhoodId =
	          CommonUtil.getPlaceParam(BusinessPlace.TOKEN, AttributeKey.NEIGHBORHOOD_ID);
	      if (!FieldVerifier.isEmpty(neighborhoodId) && FieldVerifier.isNumber(neighborhoodId)) {
	        place.setNeighborhoodId(Long.parseLong(neighborhoodId));
	      }

	      String accountId = CommonUtil.getPlaceParam(BusinessPlace.TOKEN, AttributeKey.ACCOUNT_ID);
	      if (!FieldVerifier.isEmpty(accountId) && FieldVerifier.isNumber(accountId)) {
	        place.setAccountId(Long.parseLong(accountId));
	      }

	      String gender = CommonUtil.getPlaceParam(BusinessPlace.TOKEN, AttributeKey.GENDER_KEY);
	      if (!FieldVerifier.isEmpty(gender)) {
	        place.setGender(Gender.valueOf(gender));
	      } else {
	        place.setGender(Gender.ALL);
	      }

	      return place;
	    } catch (Exception ex) {
	      return place;
	    }
	 }
	 
	private void setupHandlers() {
		residentList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				state.setRange(event.getNewRange());
				presenter.getPersonalAccountList(state.getCurrentEntityListAction());
			}
		});
		
		neighborhoodListBox.addChangeHandler(new ChangeHandler() {

			@Override
      public void onChange(ChangeEvent event) {
				long neighborhoodId = neighborhoods.get(neighborhoodListBox.getSelectedIndex()).getNeighborhoodId();
				state.searchByNeighborhood(neighborhoodId);
				presenter.getPersonalAccountList(state.getCurrentEntityListAction());
      }
			
		});
		
		genderListBox.addChangeHandler(new ChangeHandler() {

			@Override
      public void onChange(ChangeEvent event) {
				String genderName = genderListBox.getValue(genderListBox.getSelectedIndex());
				state.searchByGender(genderList.get(genderName));
				presenter.getPersonalAccountList(state.getCurrentEntityListAction());
      }
			
		});
	}

	public void displayNeighborhoodFilters(List<NeighborhoodDTO> neighborhoods) {
		if (neighborhoods != null) {
			neighborhoodListBox.clear();
			this.neighborhoods = neighborhoods;
			for (NeighborhoodDTO n : neighborhoods) {
				neighborhoodListBox.addItem(n.getName());
			}
		}
	}

	public void display(List<PersonalAccountDTO> input) {
		resetUiElements();
		if (input.size() == 0) {
			displayNoResult();
		}
		residentList.setRowData(state.getStart(), input);
	}

	private void resetUiElements() {
		enableButtonIfRequired();
		StyleHelper.show(message.getElement(), false);
		StyleHelper.show(status.getElement(), false);
	}

	private void enableButtonIfRequired() {
		if (!searchBtn.isEnabled()) {
			searchBtn.setEnabled(true);
		}

		if (!resetBtn.isEnabled()) {
			resetBtn.setEnabled(true);
		}
	}

	private void displayNoResult() {
		residentList.setRowCount(0);
		message.setText(StringConstants.NO_RESULT_FOUND);
		message.setType(AlertType.WARNING);
		StyleHelper.show(message.getElement(), true);
	}

	public void setTotalRowCount(Long count) {
		residentList.setRowCount(count.intValue(), true);
	}

	@UiHandler("searchBtn")
	void search(ClickEvent event) {
		searchBtn.setEnabled(false);
		Gender gender = genderList.get(genderListBox.getSelectedIndex());
		state.searchByGender(gender);
		state.setNeighborhood(neighborhoods
		    .get(neighborhoodListBox.getSelectedIndex())
		    .getNeighborhoodId());
		presenter.getPersonalAccountList(state.getCurrentEntityListAction());
	}

	@UiHandler("resetBtn")
	void resetSearch(ClickEvent event) {
		resetBtn.setEnabled(false);
		state.reset();
		setSelectedGender(state.getGender());
		state.setNeighborhood(neighborhoods.get(0).getNeighborhoodId());
		presenter.getPersonalAccountList(state.getCurrentEntityListAction());
	}

	@Override
	public void setPresenter(EntityListViewPresenter presenter) {
		this.presenter = presenter;
	}

	public int getPageSize() {
		return state.getPageSize();
	}

	@Override
	public void clear() {
		genderListBox.setSelectedIndex(Gender.ALL.ordinal());
		resetUiElements();
		StyleHelper.clearBackground();
	}

	public void displaySendMessageWidget(Long receiverAccountId) {
		AccountDTO receiver = new AccountDTO();
		receiver.setAccountId(receiverAccountId);
		smw = new SendMessageWidget(receiver);
		smw.setPresenter((SendMessagePresenter) presenter);
		smw.show();
	}

	public void updateMessageWidget(AccountDTO account) {
		if (smw != null) {
			smw.updateAccountInformation(account);
		}
	}

	public void displayMessage(String msg, AlertType type) {
		status.setText(msg);
		status.setType(type);
		StyleHelper.show(status.getElement(), true);
	}

	public void setBackground(NeighborhoodDTO neighborhood) {
		StyleHelper.setBackgroundImage(basicDataFormatter.format(
		    neighborhood,
		    ValueType.NEIGHBORHOOD_IMAGE));
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		for(NeighborhoodDTO n : neighborhoods) {
			if (n.getNeighborhoodId().equals(neighborhoodId)) {
				neighborhoodListBox.setSelectedValue(n.getName());
				state.setNeighborhood(neighborhoodId);
			}
		}
  }

	public void setGender(Gender gender) {
	  setSelectedGender(gender);
  }
	
  private void setSelectedGender(Gender gender) {
    String selectedValue = basicDataFormatter.format(state.getGender(), ValueType.GENDER);
    genderListBox.setSelectedValue(selectedValue);
  }
}
