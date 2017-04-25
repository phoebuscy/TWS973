/* Copyright (C) 2017 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.TestJavaClient;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class NewsArticleDlg extends JDialog {

    public boolean m_rc;

    private JTextField 	m_requestId = new JTextField("0");
    private JTextField 	m_providerCode = new JTextField();
    private JTextField 	m_articleId = new JTextField();
    private JButton 	m_ok = new JButton( "OK");
    private JButton 	m_cancel = new JButton( "Cancel");

    int m_retRequestId;
    String m_retProviderCode;
    String m_retArticleId;

    public NewsArticleDlg( JFrame owner) {
        super( owner, true);

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( m_ok);
        buttonPanel.add( m_cancel);

        // create action listeners
        m_ok.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onOk();
            }
        });
        m_cancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                onCancel();
            }
        });
        
        // create mid summary panel
        JPanel midPanel = new JPanel( new GridLayout( 0, 1, 5, 5) );
        midPanel.add( new JLabel( "Request Id") );
        midPanel.add( m_requestId);
        midPanel.add( new JLabel( "Provider Code") );
        midPanel.add( m_providerCode);
        midPanel.add( new JLabel( "Article Id") );
        midPanel.add( m_articleId);

        // create dlg box
        getContentPane().add( midPanel, BorderLayout.CENTER);
        getContentPane().add( buttonPanel, BorderLayout.SOUTH);
        setTitle( "Request News Article");
        pack();
    }

    void onOk() {
        m_rc = false;

        try {
            m_retRequestId = Integer.parseInt( m_requestId.getText());
            m_retProviderCode = m_providerCode.getText().trim();
            m_retArticleId = m_articleId.getText().trim();
        }
        catch( Exception e) {
            Main.inform( this, "Error - " + e);
            return;
        }

        m_rc = true;
        setVisible( false);
    }

    void onCancel() {
        m_rc = false;
        setVisible( false);
    }
}