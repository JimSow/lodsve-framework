package lodsve.transaction.action;

import lodsve.transaction.channel.Pay;
import lodsve.transaction.domain.Payment;
import lodsve.transaction.domain.Refund;
import lodsve.transaction.enums.TradeResult;
import lodsve.transaction.exception.RefundException;
import lodsve.transaction.repository.PaymentRepository;
import lodsve.transaction.repository.RefundRepository;
import lodsve.transaction.utils.TradeRouting;
import lodsve.transaction.utils.data.RefundData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 退款.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 15/11/5 下午7:36
 */
@Component
public abstract class AbstractRefundAction implements RefundAction {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private TradeRouting tradeRouting;

    @Override
    public void refund(Long paymentId) throws Exception {
        Assert.notNull(paymentId);
        Payment payment = paymentRepository.findOne(paymentId);
        Assert.notNull(payment);

        // 处理各个支付的一些情况
        Pay pay = tradeRouting.pay(payment.getTradeChannel());
        Assert.notNull(pay, "支付渠道错误!");

        // 判断是否曾经进行退款
        Refund refund = this.refundRepository.findByPaymentId(paymentId);

        if (refund == null) {
            refund = this.prepare(paymentId);
        } else {
            if (TradeResult.YES == refund.getTradeResult()) {
                throw new RefundException(106003, "this payment is refunded success!payment id is " + paymentId);
            }
        }

        beforeRefund(payment);

        RefundData refundData = new RefundData();
        refundData.setPayment(payment);

        Object object;
        try {
            object = pay.refund(refundData);
            // 成功之后 修改退款单状态
            if (object instanceof com.pingplusplus.model.Refund) {
                pay.changeRefundStatus((com.pingplusplus.model.Refund) object, refund);
            } else {
                pay.changeRefundStatus(null, refund);
            }

            this.refundRepository.save(refund);
        } catch (Exception e) {
            occurException(paymentId, payment.getUserId(), e);
            throw new RefundException(106004, e.getMessage());
        }
    }

    private Refund prepare(Long paymentId) {
        Assert.notNull(paymentId, "paymentId 不能为空");

        Refund refund = build(paymentId);

        this.refundRepository.save(refund);

        afterBuild(refund);
        return refund;
    }

    /**
     * 创建退款单
     *
     * @param paymentId 支付单号
     * @return
     */
    public abstract Refund build(Long paymentId);

    /**
     * 创建退款单之后执行
     *
     * @param refund 退款单
     */
    public abstract void afterBuild(Refund refund);

    /**
     * 发生异常
     *
     * @param paymentId 支付单ID
     * @param userId    用户ID
     * @param e         异常
     */
    public abstract void occurException(Long paymentId, Long userId, Exception e);
}
