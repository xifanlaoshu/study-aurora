/*
 * Created on 2008-3-1
 */
package aurora.bm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;

public class ModelFactory implements IModelFactory {
    
    //public static final String PROC_GENERATE_SQL = "aurora.database.sql.GenerateSqlStatement"; 
    
    public static final String DEFAULT_MODEL_EXTENSION = "bm";

    UncertainEngine         mUncertainEngine;
    
    CompositeLoader         mCompositeLoader; 
    
    // name -> BusinessModel
    Map                     mModelCache;
    
    public ModelFactory(UncertainEngine  engine){
        mUncertainEngine = engine;
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mCompositeLoader.setDefaultExt(DEFAULT_MODEL_EXTENSION);
        mModelCache = new HashMap();
    }
    
    /**
     * Get a model instance. The returned model is for read only.
     * Invoker shall not make any modification to returned model.
     * @param name name of model
     * @return A read-only BusinessModel instance
     * @throws IOException
     */
    public BusinessModel getModelForRead( String name )
        throws IOException
    {
        BusinessModel model = (BusinessModel)mModelCache.get(name);
        if(model==null){
            model = getNewModelInstance( name );
            mModelCache.put(name, model);
        }
        return model;
    }
    
    public BusinessModel getModel( CompositeMap config ){
        BusinessModel model = new BusinessModel();
        model.setModelFactory(this);
        model.initialize(config);
        model.makeReady();
        mModelCache.put(model.getName(), model);
        return model;        
    }

    public CompositeMap getModelConfig( String name )
        throws IOException
    {
        CompositeMap config = mUncertainEngine.loadCompositeMap(name);
        if(config==null) throw new IOException("Can't load resource "+name);
        return config;        
    }
    
    protected BusinessModel getNewModelInstance( String name )
        throws IOException
    {
        if(name==null)
            throw new IllegalArgumentException("model name is null");
        CompositeMap config = getModelConfig(name);
        BusinessModel model = getModel( config );
        model.setName(name);
        return model;
    }
    
    
    public BusinessModel getModel( String name )
        throws IOException
    {
        return getNewModelInstance(name);
    }    


}
