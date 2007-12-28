/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * gstreamer-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gstreamer-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with gstreamer-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.lowlevel;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.util.HashMap;
import org.gstreamer.GObject;
import org.gstreamer.glib.GQuark;

/**
 *
 */
public interface GObjectAPI extends Library {
    static GObjectAPI gobj = (GObjectAPI) GNative.loadLibrary("gobject-2.0", GObjectAPI.class, new HashMap<String, Object>() {{
        put(Library.OPTION_TYPE_MAPPER, new GTypeMapper());
    }});
    
    GType g_object_get_type();
    void g_object_set_property(GObject obj, String property, Object data);
    void g_object_set(GObject obj, String propertyName, Object... data);
    void g_object_get(GObject obj, String propertyName, Object... data);
    Pointer g_object_new(GType object_type, Object... args);
    
    interface GClosureNotify extends Callback {
        void callback(Pointer data, Pointer closure);
    }
    NativeLong g_signal_connect_data(GObject obj, String signal, Callback callback, Pointer data,
            GClosureNotify destroy_data, int connect_flags);
    void g_signal_handler_disconnect(GObject obj, NativeLong id);
    boolean g_object_is_floating(GObject obj);
    interface GToggleNotify extends Callback {
        void callback(Pointer data, Pointer obj, boolean is_last_ref);
    }
    void g_object_add_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    void g_object_remove_toggle_ref(Pointer object, GToggleNotify notify, Pointer data);
    void g_object_add_toggle_ref(Pointer object, GToggleNotify notify, IntPtr data);
    void g_object_remove_toggle_ref(Pointer object, GToggleNotify notify, IntPtr data);
    interface GWeakNotify extends Callback {
        void callback(IntPtr data, Pointer obj);
    }
    void g_object_weak_ref(GObject object, GWeakNotify notify, IntPtr data);
    void g_object_weak_unref(GObject object, GWeakNotify notify, IntPtr data);
    
    GQuark g_quark_try_string(String string);
    GQuark g_quark_from_static_string(String string);
    GQuark g_quark_from_string(String string);
    String g_quark_to_string(GQuark quark);

    String g_intern_string(String string);
    String g_intern_static_string(String string);
    
    void g_type_init();
    void g_type_init_with_debug_flags(int flags);
    String g_type_name(GType type);
    //GQuark                g_type_qname                   (GType            type);
    GType g_type_from_name(String name);
    GType g_type_parent(GType type);
    int g_type_depth(GType type);
    Pointer g_type_create_instance(GType type);
    void g_type_free_instance(Pointer instance);
    
    GType g_type_register_static(GType parent_type, String type_name,
        GTypeInfo info, /* GTypeFlags */ int flags);
    GType g_type_register_static(GType parent_type, Pointer type_name,
        GTypeInfo info, /* GTypeFlags */ int flags);
    GType g_type_register_static_simple(GType parent_type, String type_name,
        int class_size, GClassInitFunc class_init, int instance_size,
        GInstanceInitFunc instance_init, /* GTypeFlags */ int flags);
    GType g_type_register_static_simple(GType parent_type, Pointer type_name,
        int class_size, GClassInitFunc class_init, int instance_size,
        GInstanceInitFunc instance_init, /* GTypeFlags */ int flags);
    /* 
     * Basic Type Structures
     */
    public static final class GTypeClass extends com.sun.jna.Structure {

        /*< private >*/
        public volatile GType g_type;
    }

    public static final class GTypeInstance extends com.sun.jna.Structure {

        /*< private >*/
        public volatile Pointer g_class;
    }                  
    static class GValue extends com.sun.jna.Structure {
        /*< private >*/
        public volatile GType g_type;

        /* public for GTypeValueTable methods */
        public static class GValueData extends com.sun.jna.Union {
            int v_int;
            long v_long;
            long v_int64;            
            float v_float;
            double v_double;
            Pointer v_pointer;
        }
        public GValueData data[] = new GValueData[2];
    }
    static class GObjectStruct extends com.sun.jna.Structure {
        public volatile GTypeInstance g_type_instance;
        public volatile int ref_count;
        public volatile Pointer qdata;
        public GObjectStruct() {}
        public GObjectStruct(Pointer ptr) {
            useMemory(ptr);
            read();
        }
    }
    static public class GObjectConstructParam {
        public volatile Pointer spec;
        public volatile Pointer value;
    }
    public static final class GObjectClass extends com.sun.jna.Structure {
        public volatile GTypeClass g_type_class;
        public volatile Pointer construct_properties;
        public Constructor constructor;
        public SetProperty set_property;
        public GetProperty get_property;
        public Dispose dispose;
        public Finalize finalize;
        public volatile Pointer dispatch_properties_changed;
        public Notify notify;
        public volatile byte[] p_dummy = new byte[8 * Pointer.SIZE];
        
        public static interface Constructor extends Callback {
            public Pointer callback(GType type, int n_construct_properties, 
                    GObjectConstructParam properties);
        };
        public static interface SetProperty extends Callback {
            public void callback(GObject object, int property_id, Pointer value, Pointer spec);
        }
        public static interface GetProperty extends Callback {
            public void callback(GObject object, int property_id, Pointer value, Pointer spec);
        }
        public static interface Dispose extends Callback {
            public void callback(GObject object);
        }
        public static interface Finalize extends Callback {
            public void callback(GObject object);
        }
        public static interface Notify extends Callback {
            public void callback(GObject object, Pointer spec);
        }
    }
    
    
    public static interface GBaseInitFunc extends Callback {
        public void callback(Pointer g_class);
    }

    public static interface GBaseFinalizeFunc extends Callback {
        public void callback(Pointer g_class);
    }

    public static interface GClassInitFunc extends Callback {
        public void callback(Pointer g_class, Pointer class_data);
    }

    public static interface GClassFinalizeFunc extends Callback {
        public void callback(Pointer g_class, Pointer class_data);
    }
    public static interface GInstanceInitFunc extends Callback {
        void callback(GTypeInstance instance, Pointer g_class);
    }    
    public static final class GTypeInfo extends com.sun.jna.Structure {
        public GTypeInfo() { 
            clear();
        }
        public GTypeInfo(Pointer ptr) { 
            useMemory(ptr); 
            read();
        }
        /* interface types, classed types, instantiated types */
        public short class_size;
        public GBaseInitFunc base_init;
        public GBaseFinalizeFunc base_finalize;
        /* interface types, classed types, instantiated types */
        public GClassInitFunc class_init;
        public GClassFinalizeFunc class_finalize;
        public Pointer class_data;
        /* instantiated types */
        public short instance_size;
        public short n_preallocs;
        
        public GInstanceInitFunc instance_init;

        /* value handling */
        public volatile /* GTypeValueTable */ Pointer value_table;                
    }
}
