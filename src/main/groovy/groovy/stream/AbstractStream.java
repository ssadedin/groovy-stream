package groovy.stream ;

import groovy.lang.Closure ;
import java.util.List ;
import java.util.Map ;
import java.util.HashMap ;

public abstract class AbstractStream<T,D> implements StreamInterface<T> {
  protected int streamIndex = -1 ;
  protected boolean exhausted = false ;
  protected RuntimeException initialisationException = null ;
  Closure<D> definition ;
  Closure<Boolean> condition ;
  Closure<T> transform ;
  Map using ;
  D initial ;
  T current ;
  private boolean initialised ;

  public AbstractStream( Closure<D> definition, Closure<Boolean> condition, Closure<T> transform, Map using ) {
    this.using = using ;

    this.definition = definition ;
    this.definition.setDelegate( this.using ) ;
    this.definition.setResolveStrategy( Closure.DELEGATE_FIRST ) ;

    this.condition = condition ;
    this.condition.setDelegate( this.using ) ;
    this.condition.setResolveStrategy( Closure.DELEGATE_FIRST ) ;

    this.transform = transform ;
    this.transform.setDelegate( this.using ) ;
    this.transform.setResolveStrategy( Closure.DELEGATE_FIRST ) ;
  }

  protected Closure<D> getDefinition() { return definition ; }
  protected Closure<Boolean> getCondition() { return condition ; }
  protected Closure<T> getTransform() { return transform ; }
  protected Map getUsing() { return using ; }

  protected abstract void initialise() ;

  public int getStreamIndex() {
    return streamIndex ;
  }

  public boolean isExhausted() {
    return exhausted ;
  }

  protected abstract void loadNext() ;

  @Override
  public boolean hasNext() {
    if( initialisationException != null ) {
      throw initialisationException ;
    } 
    if( !initialised ) {
      initialise() ;
      loadNext() ;
      initialised = true ;
    }
    return current != null && !exhausted ;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException( "Cannot call remove() on a Stream" ) ;
  }
}