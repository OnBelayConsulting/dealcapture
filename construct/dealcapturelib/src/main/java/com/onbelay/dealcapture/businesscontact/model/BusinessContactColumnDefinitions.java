package com.onbelay.dealcapture.businesscontact.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "businessContactColumnDefinitions")
public class BusinessContactColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition businessContactId = new ColumnDefinition("businessContactId", ColumnDataType.INTEGER, "id");
	public static final ColumnDefinition firstName = new ColumnDefinition("firstName", ColumnDataType.STRING, "detail.firstName");
	public static final ColumnDefinition lastName = new ColumnDefinition("lastName", ColumnDataType.STRING, "detail.lastName");
	public static final ColumnDefinition email = new ColumnDefinition("email", ColumnDataType.STRING, "detail.email");
	public static final ColumnDefinition isCompanyTrader = new ColumnDefinition("isCompanyTrader", ColumnDataType.BOOLEAN, "detail.isCompanyTrader");
	public static final ColumnDefinition isCounterpartyTrader = new ColumnDefinition("isCounterpartyTrader", ColumnDataType.BOOLEAN, "detail.isCounterpartyTrader");
	public static final ColumnDefinition isAdministrator = new ColumnDefinition("isAdministrator", ColumnDataType.BOOLEAN, "detail.isAdministrator");

	public BusinessContactColumnDefinitions() {
		add(businessContactId);
		add(firstName);
		add(lastName);
		add(email);
		add(isCompanyTrader);
		add(isCounterpartyTrader);
		add(isAdministrator);
	}

	@Override
	public String getCodeName() {
		return firstName.getPath();
	}

	@Override
	public String getDescriptionName() {
		return firstName.getPath();
	}
}
