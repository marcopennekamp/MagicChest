package org.tilegames.mc.magicchest.filter;

import java.util.HashMap;

public class FilteringProfileManager {
    
    private long id;

    private FilteringProfile defaultProfile;
    
    private HashMap<Long, FilteringProfile> profiles;
    
    
    
    public FilteringProfileManager () {
        defaultProfile = new FilteringProfile ();
        profiles = new HashMap<Long, FilteringProfile> ();
    }
    
    
    public FilteringProfile getDefaultProfile () {
        return defaultProfile;
    }
    
    public FilteringProfile getProfile (Long id) {
        return profiles.get (id);
    }
    
    public Long addProfile (FilteringProfile profile) {
        long id = (this.id)++;
        profiles.put (id, profile);
        return id;
    }
    
    public void removeProfile (Long id) {
        profiles.remove (id);
    }
    
    
    public void save () {
        
    }
    
    public void load () {
        
    }
    
    
    
}
