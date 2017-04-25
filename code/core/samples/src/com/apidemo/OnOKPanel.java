package com.apidemo;

import com.ib.client.OrderCondition;

import com.apidemo.util.VerticalPanel;

public abstract class OnOKPanel extends VerticalPanel {
	public abstract OrderCondition onOK();
}