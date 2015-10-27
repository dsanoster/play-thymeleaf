package common.filters;

import java.util.UUID;

import org.slf4j.MDC;

import kamon.trace.TraceLocal;
import kamon.trace.logging.MdcKeysSupport$;
import play.Logger;
import play.api.mvc.EssentialAction;
import play.api.mvc.EssentialFilter;

public class TestLoggingFilter implements EssentialFilter {

  @Override
  public EssentialAction apply(final EssentialAction next) {
    String uuid = UUID.randomUUID().toString();

    // uuid1は通常のMDCに保管する
    MDC.put("uuid1", uuid);

    // uuid2はkamonのMDCに保管する
    TraceLocal.storeForMdc("uuid2", uuid);

    // 普通に出力した場合は通常のMDC（uuid1のみ保管）が出力される
    Logger.debug("filter1");

    // withMdc()の中ではkamonのMDC（uuid2のみ保管）が出力される
    MdcKeysSupport$.MODULE$.withMdc(() -> {
      Logger.debug("filter2");
      return "";
    });

    return next;
  }
}
