package com.dataModel;

import com.commdata.mbassadorObj.MBAAccountValue;
import com.commdata.mbassadorObj.MBAHistoricalData;
import com.commdata.mbassadorObj.MBAHistoricalDataEnd;
import com.commdata.mbassadorObj.MBAPortFolio;
import com.commdata.mbassadorObj.MBAtickPrice;
import com.database.DbManager;
import com.ib.client.*;
import com.utils.TMbassadorSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.utils.TConst.AK_CONNECTED;
import static com.utils.TConst.AK_CONTRACT_DETAIL_END;
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
    private DbManager dbManager = DbManager.getInstance();
    private String m_host;
    private int m_port;
    private int m_clientid;
    private int m_orderId=90000;


    private EJavaSignal m_signal;
    private EClientSocket m_client;
    private EReader m_reader;

    private String account;
    public Symbol symbol = null;


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

    // 获取账户
    public String getAccount()
    {
        return account;
    }

    public Symbol getSymbol()
    {
        return symbol;
    }

    public void disconnect()
    {
        m_client.eDisconnect();
    }

    public int getReqId()
    {
        int reqId = dbManager.queryReqID();
        return reqId;
    }

    public int getOrderId()
    {
        return ++m_orderId;
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


    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttr attrib)
    {
        MBAtickPrice mbAtickPrice = new MBAtickPrice(tickerId, field, price, attrib);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbAtickPrice);

    }

    @Override
    public void tickSize(int tickerId, int field, int size)
    {
        int a = 1;
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice)
    {
        TickType tickType = TickType.get(field);
        if (tickType == TickType.OPEN)
        {
            int a = 1;
        }

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value)
    {
        TickType tickT = TickType.get(tickType);
        if (tickT == TickType.OPEN)
        {
            int a = 1;
        }

    }

    @Override
    public void tickString(int tickerId, int tickType, String value)
    {
        TickType tickT = TickType.get(tickType);
        if (tickT == TickType.OPEN)
        {
            int a = 1;
        }
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate)
    {
        int a = 1;
    }

    @Override
    public void orderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice)
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


    /**
     * 这个方法仅在已经调用EClientSocket对象的reqAccountUpdates()方法时调用
     * 参数	描述
     *
     * @param key         表示一种账户值类型的字符串。 有很多可被发送的可用的标签，这里仅列出几个样本：
     *                    <p>
     *                    CashBalance - 账户现金余额
     *                    DayTradesRemaining - 剩余交易日
     *                    EquityWithLoanValue - 含借贷值股权
     *                    InitMarginReq - 当前初始保证金要求
     *                    MaintMarginReq - 当前维持保证金
     *                    NetLiquidation - 净清算值
     * @param value       与标签相关的值。
     * @param currency    在值为货币类型的情况下，定义货币类型。
     * @param accountName 说明信息应用的账户。适用于金融顾问子账户信息。
     */
    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName)
    {
        MBAAccountValue mbaAccountValue = new MBAAccountValue(key, value, currency, accountName);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbaAccountValue);
    }


    /**
     * 这个方法仅在已经调用EClientSocket对象的reqAccountUpdates()方法时调用。
     * 参数	描述
     *
     * @param contract      该结构包括交易合约的描述。 合约的交易所区域没有为投资组合更新而设置。
     * @param position      该整数表示合约头寸。 如果头寸为0，表示头寸刚被平仓。
     * @param marketPrice   产品的单位价格。
     * @param marketValue   产品的总市值。
     * @param averageCost   每股的平均成本的计算是用你头寸的数量除以你的成本（执行价格＋佣金）。
     * @param unrealizedPNL 你未平仓头寸的当前市值和平均成本或平均成本值的差。
     * @param realizedPNL   显示平仓头寸的利润，为你的建仓执行成本（执行价格＋建仓佣金）和平仓执行成本（执行价格＋平仓头寸佣金）的差。
     * @param accountName   信息应用于的账户名称。适用于金融顾问子账户信息。
     */
    @Override
    public void updatePortfolio(Contract contract, double position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName)
    {
        MBAPortFolio mbaPortFolio = new MBAPortFolio(contract, position, marketPrice, marketValue, averageCost,
                                                     unrealizedPNL, realizedPNL, accountName);

        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbaPortFolio);
    }


    /**
     * 这个方法仅在已经调用EClientSocket对象的reqAccountUpdates()方法时调用。
     * 参数	描述
     *
     * @param timeStamp 表示账户信息的最后更新时间。
     */
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
        int a = 1;

    }

    @Override
    public void contractDetailsEnd(int reqId)
    {
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(
                makeAKmsg(AK_CONTRACT_DETAIL_END, String.valueOf(reqId)));
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
        int a = 1;
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size)
    {
        int a = 1;
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange)
    {

    }

    @Override
    public void managedAccounts(String accountsList)
    {
        account = accountsList;

        // 调用这个功能开始获得账户值、投资组合、和最后更新时间信息。
        // 账户数据将通过updateAccountTime()，updateAccountValue()和 updatePortfolio() EWrapper methods反馈。
        m_client.reqAccountUpdates(true, account);
    }

    @Override
    public void receiveFA(int faDataType, String xml)
    {
    }

    @Override
    public void historicalData(int reqId, Bar bar)
    {
        MBAHistoricalData mbaHistoricalData = new MBAHistoricalData(reqId, bar.time(), bar.open(), bar.high(),
                                                                    bar.low(), bar.close(), bar.volume(), bar.count(),
                                                                    bar.wap(), false);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbaHistoricalData);
    }


    @Override
    public void scannerParameters(String xml)
    {

    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr)
    {

    }

    @Override
    public void scannerDataEnd(int reqId)
    {

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count)
    {
        int a = 1;
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
            m_client.reqManagedAccts(); // 查询账户
        }
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, double pos, double avgCost)
    {

    }

    @Override
    public void positionMultiEnd(int reqId)
    {

    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency)
    {

    }

    @Override
    public void accountUpdateMultiEnd(int reqId)
    {

    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes)
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
        MBAHistoricalDataEnd mbaHistoricalDataEnd = new MBAHistoricalDataEnd(reqId, startDateStr, endDateStr);
        TMbassadorSingleton.getInstance(DATAMAAGER_BUS).publish(mbaHistoricalDataEnd);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions)
    {

    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData)
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
    public void histogramData(int reqId, List<HistogramEntry> items)
    {
    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar)
    {

    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange)
    {

    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange)
    {

    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements)
    {

    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL)
    {

    }

    @Override
    public void pnlSingle(int reqId, int pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value)
    {

    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done)
    {

    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done)
    {

    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done)
    {

    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, int size, TickAttr attribs, String exchange, String specialConditions)
    {

    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, int bidSize, int askSize, TickAttr attribs)
    {

    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint)
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
