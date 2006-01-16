/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.rsf.processor;

import java.util.Map;

import uk.org.ponder.errorutil.ThreadErrorState;
import uk.org.ponder.rsf.flow.ARIResolver;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionErrorStrategy;
import uk.org.ponder.rsf.flow.ActionResultInterpreter;
import uk.org.ponder.rsf.flow.ViewExceptionStrategy;
import uk.org.ponder.rsf.preservation.StatePreservationManager;
import uk.org.ponder.rsf.request.RequestSubmittedValueCache;
import uk.org.ponder.rsf.state.ErrorStateManager;
import uk.org.ponder.rsf.state.RSVCApplier;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.RunnableWrapper;

/**
 * ActionHandler is a request scope bean responsible for handling an HTTP POST
 * request, or other non-idempotent web service "action" cycle. Defines the core
 * logic for this processing cycle.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
public class RSFActionHandler implements ActionHandler {
  // application-scope dependencies
  private ARIResolver ariresolver;
  private RunnableWrapper postwrapper;
  private RequestSubmittedValueCache requestrsvc;

  // request-scope dependencies
  private Map normalizedmap;
  private ViewParameters viewparams;
  private ErrorStateManager errorstatemanager;
  private RSVCApplier rsvcapplier;
  private StatePreservationManager presmanager; // no, not that of OS/2
  private ViewExceptionStrategy ves;
  private ActionErrorStrategy actionerrorstrategy;

  public void setErrorStateManager(ErrorStateManager errorstatemanager) {
    this.errorstatemanager = errorstatemanager;
  }

  public void setARIResolver(ARIResolver ariresolver) {
    this.ariresolver = ariresolver;
  }

  public void setViewParameters(ViewParameters viewparams) {
    this.viewparams = viewparams;
  }

  public void setRequestRSVC(RequestSubmittedValueCache requestrsvc) {
    this.requestrsvc = requestrsvc;
  }

  public void setAlterationWrapper(RunnableWrapper postwrapper) {
    this.postwrapper = postwrapper;
  }

  public void setRSVCApplier(RSVCApplier rsvcapplier) {
    this.rsvcapplier = rsvcapplier;
  }

  public void setNormalizedRequestMap(Map normalizedmap) {
    this.normalizedmap = normalizedmap;
  }

  public void setStatePreservationManager(StatePreservationManager presmanager) {
    this.presmanager = presmanager;
  }

  public void setViewExceptionStrategy(ViewExceptionStrategy ves) {
    this.ves = ves;
  }

  public void setActionErrorStrategy(ActionErrorStrategy actionerrorstrategy) {
    this.actionerrorstrategy = actionerrorstrategy;
  }

  private ARIResult ariresult = null;

  /**
   * The result of this post cycle will be of interest to some other request
   * beans, in particular the alteration wrapper. This bean must however be
   * propagated lazily since it is only constructed partway through this
   * handler, if indeed it is a POST cycle at all.
   * 
   * @return
   */
  public ARIResult getARIResult() {
    return ariresult;
  }

  // Since this entire bean is request scope, there is no difficulty with
  // letting the action result escape from the wrapper into this instance
  // variable.
  private Object actionresult = null;
  private Exception exception;

  public ViewParameters handle() {
    final String actionmethod = PostDecoder.decodeAction(normalizedmap);

    try {
      // invoke all state-altering operations within the runnable wrapper.
      postwrapper.wrapRunnable(new Runnable() {
        public void run() {
          if (viewparams.flowtoken != null) {
            presmanager.restore(viewparams.flowtoken,
                viewparams.endflow != null);
          }
          rsvcapplier.applyValues(requestrsvc); // many errors possible here.

          if (actionmethod != null) {
            try {
              actionresult = rsvcapplier.invokeAction(actionmethod);
            }
            catch (Exception exception) {
              RSFActionHandler.this.exception = exception;
            }
            actionerrorstrategy.handleError(
                actionresult instanceof String ? (String) actionresult
                    : null, exception, null, viewparams.viewID);
          }
          // must interpret ARI INSIDE the wrapper, since it may need it
          // on closure.
          if (actionresult instanceof ARIResult) {
            ariresult = (ARIResult) actionresult;
          }
          else {
            ActionResultInterpreter ari = ariresolver
                .getActionResultInterpreter();
            ariresult = ari.interpretActionResult(viewparams, actionresult);
          }
        }
      }).run();

      if (!ariresult.propagatebeans.equals(ARIResult.FLOW_END)) {
        // TODO: consider whether we want to allow ARI to allocate a NEW TOKEN
        // for a FLOW FORK. Some call this, "continuations".
        if (ariresult.resultingview.flowtoken == null
            && ariresult.propagatebeans.equals(ARIResult.FLOW_START)) {
          // if the ARI wanted one and hasn't allocated one, allocate flow
          // token.
          ariresult.resultingview.flowtoken = errorstatemanager.allocateToken();
        }
        // On a FLOW_START, **ONLY** the flow state itself is to be saved,
        // since any other existing bean state will be non-flow or end-flow.
        presmanager.preserve(ariresult.resultingview.flowtoken, 
            ariresult.propagatebeans.equals(ARIResult.FLOW_START));
      }
      else { // it is a flow end.
        ariresult.resultingview.endflow = "1";
        if (viewparams.flowtoken != null) {
          presmanager.flowEnd(viewparams.flowtoken);
        }
      }
    }
    catch (Exception e) {
      Logger.log.error("Error invoking action", e);
      // ThreadErrorState.addError(new TargettedMessage(
      // CoreMessages.GENERAL_ACTION_ERROR));
      // Detect failure to fill out arires properly.
      if (ariresult == null || ariresult.resultingview == null) {
        ariresult = new ARIResult();
        ariresult.propagatebeans = ARIResult.FLOW_END;

        ViewParameters defaultparameters = ves.handleException(e, viewparams);
        ariresult.resultingview = defaultparameters;
      }
    }
    String submitting = PostDecoder.decodeSubmittingControl(normalizedmap);
    errorstatemanager.globaltargetid = submitting;

    String errortoken = errorstatemanager.requestComplete();

    ariresult.resultingview.errortoken = errortoken;
    return ariresult.resultingview;
  }

}