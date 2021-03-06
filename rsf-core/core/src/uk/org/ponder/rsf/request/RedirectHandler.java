/*
 * Created on May 3, 2006
 */
package uk.org.ponder.rsf.request;

/** An interface used primarily from a RootHandlerBean to request an "internal"
 * redirect action from its environment. Typically the environment will cause a 
 * stoppage and restart of the RSAC environment, and then replace the 
 * EarlyRequestParser data with that supplied before reissuing the request.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
// TODO: Remove this infrastructure (only used in RSFPortlet) in favour of
// the "LazarusRedirector"
public interface RedirectHandler {
  public void handleRedirect(InternalRedirectState restate);
}
