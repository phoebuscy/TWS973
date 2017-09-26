package com.dataModel;

import com.dataModel.mbassadorObj.MBAtickPrice;
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
import com.ib.client.TagValue;
import com.ib.client.TickAttr;
import com.utils.TMbassadorSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.utils.SUtil.getDate;
import static com.utils.SUtil.getSysYear;
import static com.utils.TConst.AK_CONNECTED;
import static com.utils.TConst.AK_CONTRACT_DETAIL_END;
import static com.utils.TConst.DATAMAAGER_BUS;
import static com.utils.TPubUtil.makeAKmsg;
import static com.utils.TStringUtil.notNullAndEmptyStr;
import static com.utils.TStringUtil.nullOrEmptyStr;

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

    public Symbol symbol = null;

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
        symbol = new Symbol(this);
    }


    public static SDataManager getInstance()
    {
        return instance;
    }

    public boolean isConnected()
    {
        return (m_client != null && (m_client.isConnected() || m_client.isAsyncEConnect()));
    }

    public void setClientConnectionParam(String host, int port, int clientid)
    {
        m_host = host;
        m_port = port;
        m_clientid = clientid;
    }

    public void connect()
    {
        if (notNullAndEmptyStr(m_host) && m_port > 0 && m_clientid > 0)
        {
            if (m_client != null && (m_client.isConnected() || m_client.isAsyncEConnect()))
            {
                return;
            }
            m_signal = new EJavaSignal();
            m_client = new EClientSocket(this, m_signal);
            m_client.eConnect(m_host, m_port, m_clientid);
            m_reader = new EReader(m_client, m_signal);
            m_reader.start();
        }
    }

    public Symbol getSymbol()
    {
        return symbol;
    }

    public void disconnect()
    {
        m_client.eDisconnect();
    }

    public static synchronized int getReqId()
    {
        reqId++;
        return reqId;
    }


    public void orderTick()
    {
        Contract contract = new Contract();
        contract.conid(0);
        contract.symbol("IBM");
        contract.secType("STK");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");
        contract.currency("USD");
        m_client.reqMktData(getReqId(), contract, "mdoff,292", false, false, null);
    }

    /**
     * 获取历史数据
     *
     * @param symbol
     * @param endDataTime
     * @param durationStr
     * @param barSize
     */
    public void reqHistoryDatas(String symbol, String endDataTime, String durationStr, String barSize)
    {
        String t_symbol = nullOrEmptyStr(symbol) ? "SPY" : symbol;
        String t_endDataTime = nullOrEmptyStr(endDataTime) ? "20170726 12:00:00" : endDataTime;
        String t_durationStr = nullOrEmptyStr(durationStr) ? "1 D" : durationStr;
        String t_barSize = nullOrEmptyStr(barSize) ? "1 minute" : barSize;

        Contract contract = new Contract();
        contract.conid(0);
        contract.symbol(t_symbol);
        contract.secType("STK");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");
        contract.currency("USD");

        String whatToShow = "TRADES";
        int useRTH = 0;
        int formatData = 2;
        List<TagValue> tagValueList = new ArrayList<>();

        m_client.reqHistoricalData(getReqId(),
                                   contract,
                                   t_endDataTime,
                                   t_durationStr,
                                   t_barSize,
                                   whatToShow,
                                   useRTH,
                                   formatData,
                                   tagValueList);
    }


    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttr attrib)
    {
        MBAtickPrice mbAtickPrice = new MBAtickPrice(tickerId,field,price,attrib);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbAtickPrice);

    }

    @Override
    public void tickSize(int tickerId, int field, int size)
    {
        int a = 1;
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
        int a = 1;

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
        if (reqId > 0 && contractDetails != null)
        {
            TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(reqId, contractDetails));
        }
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails)
    {

    }

    @Override
    public void contractDetailsEnd(int reqId)
    {
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(makeAKmsg(AK_CONTRACT_DETAIL_END,
                                                                          String.valueOf(reqId)));
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
        StringBuilder strBuilder = new StringBuilder(100);

        strBuilder.append("reqId:");
        strBuilder.append(reqId);
        strBuilder.append("/");
        strBuilder.append("date:");
        String d_date = getDate(date);
        strBuilder.append(d_date);
        strBuilder.append("/");
        strBuilder.append("open:");
        strBuilder.append(open);
        strBuilder.append("/");
        strBuilder.append("high:");
        strBuilder.append(high);
        strBuilder.append("/");
        strBuilder.append("low:");
        strBuilder.append(low);
        strBuilder.append("/");
        strBuilder.append("close:");
        strBuilder.append(close);
        strBuilder.append("/");
        strBuilder.append("volume:");
        strBuilder.append(volume);
        strBuilder.append("/");
        strBuilder.append("count:");
        strBuilder.append(count);
        strBuilder.append("/");
        strBuilder.append("WAP:");
        strBuilder.append(WAP);
        strBuilder.append("/");
        strBuilder.append("hasGaps:");
        strBuilder.append(hasGaps);

        LogMsg.info(strBuilder.toString());

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
        Date dt = new Date(time * 1000);
        String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
        int a = 1;
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
        if (m_client != null)
        {
            m_client.reqCurrentTime();
        }
    }


}
