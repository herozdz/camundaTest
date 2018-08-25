package org.camunda.bpm.getstarted.loanapproval;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 2018/8/24
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class CalculateInterestService implements JavaDelegate {

    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("Spring Bean (CalculateInterestService) invoked.");
    }
}
