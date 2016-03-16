/*

import lius.exception.LiusException;



import org.apache.log4j.Logger;



/**
 * Classe utilisant des Threads pour supprimer des documents de l'index.
 * <br/><br/>
 * Class using threads to delete documents from index.
 *
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 */
public class ThreadDeleteDoc
    extends Thread {
  static Logger logger = Logger.getRootLogger();
  private String dir = "";
  private String field = "";
  private String content = "";
  private Term term = null;
  private boolean populated = false;
  private boolean populatedWithTerm = false;


  public ThreadDeleteDoc(String dir, String field, String content) {
    this.dir = dir;
    this.field = field;
    this.content = content;
    populated = true;

  }

  public ThreadDeleteDoc(String dir, Term term) {
    this.dir = dir;
    this.term = term;
    boolean populatedWithTerm = true;
  }

  public void run() {
    if (populated == true) {

      try {
        LuceneActions.getSingletonInstance().deleteDoc(dir, field, content);
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
          logger.error(ex.getMessage());
        }

      }
      catch (LiusException e) {
        logger.error(e.getMessage());
      }

    }
    else if (populatedWithTerm == true) {
      try {
        LuceneActions.getSingletonInstance().deleteDoc(dir, term);

      }
      catch (LiusException e) {
        logger.error(e.getMessage());
      }
    }

  }
}