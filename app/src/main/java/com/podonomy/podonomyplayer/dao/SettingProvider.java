package com.podonomy.podonomyplayer.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utility method which the extension class can extend to provide settings access
 */
public abstract class SettingProvider {
  protected List<Setting> settings = null;
  protected SettingProvider parent = null;

  /**
   * Creates a setting provider.
   * @param settings the list to user to perform the primary search
   * @param parent If a setting isn't found in the primary settings, ask the parent for it.
   */
  public SettingProvider(List<Setting> settings, SettingProvider parent){
    this.settings = settings;
    this.parent   = parent;
  }

  public Integer getInt(String name){
    Setting s = find(name);
    if (s == null && parent != null)
      s = parent.find(name);

    if (s == null )
      return null;
    return Integer.parseInt(s.getValue());
  }
  public Float getFloat(String name) {
    Setting s = find(name);
    if (s == null && parent != null)
      s = parent.find(name);
    if (s == null)
      return null;
    return Float.parseFloat(s.getValue());
  }
  public Double getDouble(String name) {
    Setting s = find(name);
    if (s == null && parent != null)
      s = parent.find(name);
    if (s == null)
      return null;
    return Double.parseDouble(s.getValue());
  }
  public String getString(String name) {
    Setting s = find(name);
    if (s == null && parent != null)
      s = parent.find(name);
    if (s == null )
      return null;
    return s.getValue();
  }

  public void setString(String name, String value) {
    Setting s = find(name);
    if (s == null) {
      s = new Setting();
      s.setName(name);
      s.setValue(value);
      settings.add(s);    //don't refactor. I only want to add to the list once I know the setting is created successfully
    }
    else
      s.setValue(value);
  }
  public void setInt(String name, int i){
    Setting s = find(name);
    Setting t = new Setting();
    t.setName(name);
    t.setValue(Integer.toString(i));

    if (s != null)
      s.setValue(t.getValue());
    else
      settings.add(t);
  }
  public void setLong(String name, long l){
    Setting s = find(name);
    Setting t = new Setting();
    t.setName(name);
    t.setValue(Long.toString(l));

    if (s != null)
      s.setValue(t.getValue());
    else
      settings.add(t);
  }
  public void setDouble(String name, double d){
    Setting s = find(name);
    Setting t = new Setting();
    t.setName(name);
    t.setValue(Double.toString(d));

    if (s != null)
      s.setValue(t.getValue());
    else
      settings.add(t);
  }
  public void setFloat(String name, float f){
    Setting s = find(name);
    Setting t = new Setting();
    t.setName(name);
    t.setValue(Float.toString(f));

    if (s != null)
      s.setValue(t.getValue());
    else
      settings.add(t);
  }

  protected Setting find(String name){
    if (settings == null || name == null || StringUtils.isBlank(name))
        return null;

    for (Setting s : settings){
      if (name.equals(s.getName()))
        return s;
    }

    return null;
  }
}
