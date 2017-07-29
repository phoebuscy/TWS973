package com.dataModel;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttr;
import com.utils.TMbassadorSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.utils.TConst.AK_CONNECTED;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TPubUtil.makeAKmsg;
import static com.utils.TStringUtil.notNullAndEmptyStr;

/**
 * Created by caiyong on 2017/2/3.
 */
public class SDataManager implements EWrapper
{
    private static Logger LogApp = LogManager.getLogger("applog");
    private static Logger LogMsg = LogManager.getLogger("datamsg");

    private static SDataManager instance = new SDataManager();
    private String m_host;
    private int m_port;
    private int m_clientid;


    private EJavaSignal m_signal;
    private EClientSocket m_client;
    private EReader m_reader;

    private static int reqId = 100000;

    static
    {
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).subscribe(instance);
    }

    public static void main(String[] args)
    {
        SDataManager dmg = new SDataManager();
        dmg.orderTick();

        for (int i = 0; i < 10000; i++)
        {
            LogApp.trace("trace level");
            LogApp.debug("debug level");
            LogApp.info("info level");
            LogApp.warn("warn level");
            LogApp.error("error level");
            LogApp.fatal("fatal level");


            LogMsg.trace("trace level2");
            LogMsg.debug("debug level2");
            LogMsg.info("info level2");
            LogMsg.warn("warn level2");
            LogMsg.error("error level2");
            LogMsg.fatal("fatal level2");
        }
    }

    private SDataManager()
    {
    }


    public static SDataManager getInstance()
    {
        return instance;
    }

    public void conncet()
    {
        if (notNullAndEmptyStr(m_host) && m_port > 0 && m_clientid > 0)
        {
            connect(m_host, m_port, m_clientid);
        }
    }

    public boolean isConnected()
    {
        return (m_client != null && (m_client.isConnected() || m_client.isAsyncEConnect()));
    }

    public void connect(String host, int port, int clientid)
    {
        m_host = host;
        m_port = port;
        m_clientid = clientid;

        if(m_client != null && (m_client.isConnected() || m_client.isAsyncEConnect()))
        {
            return;
        }

        m_signal = new EJavaSignal();
        m_client = new EClientSocket(this, m_signal);

        m_client.eConnect(m_host, m_port, m_clientid);
        if(m_client.isConnected())
        {
            m_reader = new EReader(m_client, m_signal);
            m_reader.start();
        }
    }


    public void disconnect()
    {
        m_client.eDisconnect();
    }


    public void orderTick()
    {
        reqId++;
        Contract contract = new Contract();
        contract.conid(0);
        contract.symbol("IBM");
        contract.secType("STK");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");
        contract.currency("USD");
        m_client.reqMktData(reqId, contract, "mdoff,292", false, false, null);
    }



    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttr attrib)
    {

    }

    @Override
    public void tickSize(int tickerId, int field, int size)
    {

    }

    @Override
    public void tickOptionComputation(int tickerId,
                                      int field,
                                      double impliedVol,
                                      double delta,
                                      double optPrice,
                                      double pvDividend,
                                      double gamma,
                                      double vega,
                                      double theta,
                                      double undPrice)
    {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value)
    {

    }

    @Override
    public void tickString(int tickerId, int tickType, String value)
    {

    }

    @Override
    public void tickEFP(int tickerId,
                        int tickType,
                        double basisPoints,
                        String formattedBasisPoints,
                        double impliedFuture,
                        int holdDays,
                        String futureLastTradeDate,
                        double dividendImpact,
                        double dividendsToLastTradeDate)
    {

    }

    @Override
    public void orderStatus(int orderId,
                            String status,
                            double filled,
                            double remaining,
                            double avgFillPrice,
                            int permId,
                            int parentId,
                            double lastFillPrice,
                            int clientId,
                            String whyHeld)
    {

    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState)
    {

    }

    @Override
    public void openOrderEnd()
    {

    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName)
    {

    }

    @Override
    public void updatePortfolio(Contract contract,
                                double position,
                                double marketPrice,
                                double marketValue,
                                double averageCost,
                                double unrealizedPNL,
                                double realizedPNL,
                                String accountName)
    {

    }

    @Override
    public void updateAccountTime(String timeStamp)
    {

    }

    @Override
    public void accountDownloadEnd(String accountName)
    {

    }

    @Override
    public void nextValidId(int orderId)
    {

    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails)
    {

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails)
    {

    }

    @Override
    public void contractDetailsEnd(int reqId)
    {

    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution)
    {

    }

    @Override
    public void execDetailsEnd(int reqId)
    {

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size)
    {

    }

    @Override
    public void updateMktDepthL2(int tickerId,
                                 int position,
                                 String marketMaker,
                                 int operation,
                                 int side,
                                 double price,
                                 int size)
    {

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange)
    {

    }

    @Override
    public void managedAccounts(String accountsList)
    {

    }

    @Override
    public void receiveFA(int faDataType, String xml)
    {

    }

    @Override
    public void historicalData(int reqId,
                               String date,
                               double open,
                               double high,
                               double low,
                               double close,
                               int volume,
                               int count,
                               double WAP,
                               boolean hasGaps)
    {

    }

    @Override
    public void scannerParameters(String xml)
    {

    }

    @Override
    public void scannerData(int reqId,
                            int rank,
                            ContractDetails contractDetails,
                            String distance,
                            String benchmark,
                            String projection,
                            String legsStr)
    {

    }

    @Override
    public void scannerDataEnd(int reqId)
    {

    }

    @Override
    public void realtimeBar(int reqId,
                            long time,
                            double open,
                            double high,
                            double low,
                            double close,
                            long volume,
                            double wap,
                            int count)
    {

    }

    @Override
    public void currentTime(long time)
    {
        long retTime = time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date(retTime));

    }

    @Override
    public void fundamentalData(int reqId, String data)
    {

    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract underComp)
    {

    }

    @Override
    public void tickSnapshotEnd(int reqId)
    {

    }

    @Override
    public void marketDataType(int reqId, int marketDataType)
    {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport)
    {

    }

    @Override
    public void position(String account, Contract contract, double pos, double avgCost)
    {

    }

    @Override
    public void positionEnd()
    {

    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency)
    {

    }

    @Override
    public void accountSummaryEnd(int reqId)
    {

    }

    @Override
    public void verifyMessageAPI(String apiData)
    {

    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText)
    {

    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallange)
    {

    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText)
    {

    }

    @Override
    public void displayGroupList(int reqId, String groups)
    {

    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo)
    {

    }

    @Override
    public void error(Exception e)
    {
        int a = 1;
    }

    @Override
    public void error(String str)
    {

        String retErrMsg = str;
    }

    @Override
    public void error(int id, int errorCode, String errorMsg)
    {
        int errid = id;
        int errcode = errorCode;
        String retErrMsg = errorMsg;

    }

    @Override
    public void connectionClosed()
    {
        if (!m_client.isAsyncEConnect())
        {
            TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "false"));
        }
    }

    @Override
    public void connectAck()
    {
        if (m_client.isAsyncEConnect())
        {
            m_client.startAPI();
            TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONNECTED, "true"));
        }
    }

    @Override
    public void positionMulti(int reqId,
                              String account,
                              String modelCode,
                              Contract contract,
                              double pos,
                              double avgCost)
    {

    }

    @Override
    public void positionMultiEnd(int reqId)
    {

    }

    @Override
    public void accountUpdateMulti(int reqId,
                                   String account,
                                   String modelCode,
                                   String key,
                                   String value,
                                   String currency)
    {

    }

    @Override
    public void accountUpdateMultiEnd(int reqId)
    {

    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId,
                                                    String exchange,
                                                    int underlyingConId,
                                                    String tradingClass,
                                                    String multiplier,
                                                    Set<String> expirations,
                                                    Set<Double> strikes)
    {

    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId)
    {

    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers)
    {

    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes)
    {

    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions)
    {

    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr)
    {

    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions)
    {

    }

    @Override
    public void tickNews(int tickerId,
                         long timeStamp,
                         String providerCode,
                         String articleId,
                         String headline,
                         String extraData)
    {

    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap)
    {

    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions)
    {

    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders)
    {

    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText)
    {

    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline)
    {

    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore)
    {

    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp)
    {

    }

    @Override
    public void histogramData(int reqId, List<Map.Entry<Double, Long>> items)
    {

    }


    public EClientSocket getM_client()
    {
        return m_client;
    }

    public String getM_host()
    {
        return m_host;
    }

    public int getM_port()
    {
        return m_port;
    }

    public int getM_clientid()
    {
        return m_clientid;
    }

    public EJavaSignal getM_signal()
    {
        return m_signal;
    }

    public EReader getM_reader()
    {
        return m_reader;
    }


    public void reqCurrentTime()
    {
        if(m_client != null)
        {
            m_client.reqCurrentTime();
        }
    }


}
