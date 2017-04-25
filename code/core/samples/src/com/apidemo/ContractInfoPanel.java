/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.apidemo;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.Types.FundamentalType;
import com.ib.controller.ApiController.IContractDetailsHandler;
import com.ib.controller.ApiController.IFundamentalsHandler;

import com.apidemo.util.HtmlButton;
import com.apidemo.util.NewTabbedPanel;
import com.apidemo.util.NewTabbedPanel.INewTab;
import com.apidemo.util.TCombo;
import com.apidemo.util.VerticalPanel;

class ContractInfoPanel extends JPanel {
	private final Contract m_contract = new Contract();
	private final NewTabbedPanel m_resultsPanels = new NewTabbedPanel();
	
	ContractInfoPanel() {
		final NewTabbedPanel m_requestPanels = new NewTabbedPanel();
		m_requestPanels.addTab( "Contract details", new DetailsRequestPanel() );
		m_requestPanels.addTab( "Fundamentals", new FundaRequestPanel() );
		
		setLayout( new BorderLayout() );
		add( m_requestPanels, BorderLayout.NORTH);
		add( m_resultsPanels);
	}
	
	class DetailsRequestPanel extends JPanel {
		ContractPanel m_contractPanel = new ContractPanel( m_contract);
		
		DetailsRequestPanel() {
			HtmlButton but = new HtmlButton( "Query") {
				@Override protected void actionPerformed() {
					onQuery();
				}
			};

			setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
			add( m_contractPanel);
			add( Box.createHorizontalStrut(20));
			add( but);
		}
		
		void onQuery() {
			m_contractPanel.onOK();
			
			DetailsResultsPanel panel = new DetailsResultsPanel();
			m_resultsPanels.addTab( m_contract.symbol() + " " + "Description", panel, true, true);
			ApiDemo.INSTANCE.controller().reqContractDetails(m_contract, panel);
		}
	}

	static class DetailsResultsPanel extends JPanel implements IContractDetailsHandler {
		JLabel m_label = new JLabel();
		JTextArea m_text = new JTextArea();
		
		DetailsResultsPanel() {
			JScrollPane scroll = new JScrollPane( m_text);

			setLayout( new BorderLayout() );
			add( m_label, BorderLayout.NORTH);
			add( scroll);
		}

		@Override public void contractDetails(ArrayList<ContractDetails> list) {
 			// set label
			if (list.size() == 0) {
				m_label.setText( "No matching contracts were found");
			}
			else if (list.size() > 1) {
				m_label.setText( list.size() + " contracts returned; showing first contract only");
			}
			else {
				m_label.setText( null);
			}
			
			// set text
			if (list.size() == 0) {
				m_text.setText( null);
			}
			else {
				m_text.setText( list.get( 0).toString() );
			}
		}
	}
	
	public class FundaRequestPanel extends JPanel {
		ContractPanel m_contractPanel = new ContractPanel( m_contract);
		TCombo<FundamentalType> m_type = new TCombo<>( FundamentalType.values() );
		
		FundaRequestPanel() {
			HtmlButton but = new HtmlButton( "Query") {
				@Override protected void actionPerformed() {
					onQuery();
				}
			};
			
			VerticalPanel rightPanel = new VerticalPanel();
			rightPanel.add( "Report type", m_type);
			
			setLayout( new BoxLayout( this, BoxLayout.X_AXIS));
			add( m_contractPanel);
			add( Box.createHorizontalStrut(20));
			add( rightPanel);
			add( Box.createHorizontalStrut(10));
			add( but);
		}
		
		void onQuery() {
			m_contractPanel.onOK();
			FundaResultPanel panel = new FundaResultPanel();
			FundamentalType type = m_type.getSelectedItem();
			m_resultsPanels.addTab( m_contract.symbol() + " " + type, panel, true, true);
			ApiDemo.INSTANCE.controller().reqFundamentals( m_contract, type, panel); 
		}
	}	
	
	class FundaResultPanel extends JPanel implements INewTab, IFundamentalsHandler {
		String m_data;
		JTextArea m_text = new JTextArea();

		FundaResultPanel() {
			HtmlButton b = new HtmlButton( "View in browser") {
				@Override protected void actionPerformed() {
					onView();
				}
			};

			JScrollPane scroll = new JScrollPane( m_text);
			setLayout( new BorderLayout() );
			add( scroll);
			add( b, BorderLayout.EAST);
		}

		void onView() {
			try {
				File file = File.createTempFile( "tws", ".xml");
				try (FileWriter writer = new FileWriter( file)) {
					writer.write(m_text.getText());
				}
				Desktop.getDesktop().open( file);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		/** Called when the tab is first visited. */
		@Override public void activated() {
			ApiDemo.INSTANCE.controller().reqFundamentals(m_contract, FundamentalType.ReportRatios, this);
		}
		
		/** Called when the tab is closed by clicking the X. */
		@Override public void closed() {
		}

		@Override public void fundamentals(String str) {
			m_data = str;
			m_text.setText( str);
		}
	}
}
