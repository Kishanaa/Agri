package com.aurorasphere.agri;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class CropSelectionManager {

    private static final String PREF_NAME = "CropSelectionPrefs";
    private static final String KEY_STATE = "selected_state";
    private static final String KEY_CITY = "selected_city";
    private static final String KEY_CROP = "selected_crop";

    private static String selectedState = null;
    private static String selectedCity = null;
    private static String selectedCrop = null;

    // Structured local crop data
    private static final Map<String, Map<String, Map<String, CropInfo>>> cropData = new HashMap<>();

    static {
        // Sample crop data
        CropInfo GroundnutInfo = new CropInfo("June – July", "October - November", 150, 32, 20, 80, 35,"Plow the field and ensure good drainage. Sow the seeds in rows with proper spacing. Regular weeding and irrigation are essential, especially during flowering and pegging stages.",6000);
        CropInfo Rabi_SorghumInfo = new CropInfo("September", "December - January", 90, 31, 15, 90, 35,"Prepare a well-tilled seedbed. Sow seeds during September–October with appropriate spacing. Moderate irrigation is needed, and crop matures in cool dry climate.",2800);
        CropInfo Redgram_SorghumInfo = new CropInfo("June - July", "November - December", 150, 30, 20, 85, 40,"Sow seeds at the onset of monsoon with wide spacing. It requires minimal irrigation and is often intercropped. Harvest when pods turn brown and dry.",7500);
        CropInfo Rabi_SorghumM_Info = new CropInfo("September", "January - February", 120, 31, 16, 90, 35,"Prepare a well-tilled seedbed. Sow seeds during September–October with appropriate spacing. Moderate irrigation is needed, and crop matures in cool dry climate.",2800);
        CropInfo SoybeanInfo = new CropInfo("June - July", "October - November", 150, 30, 18, 90, 45,"Requires a fine seedbed with good moisture. Sow seeds during June–July. Ensure timely weeding, pest control, and adequate drainage to prevent waterlogging.",4800);
        CropInfo Pearl_SorghumInfo = new CropInfo("June - July", "September - October", 120, 32, 20, 80, 40,"Prepare light to medium textured soil. Direct sowing is done before monsoon. It requires minimal water and grows well in arid regions.",2400);

        CropInfo Soybean_SorghumInfo = new CropInfo("June - July", "October - November", 150, 30, 18, 90, 40,"Soybean is a major oilseed crop grown during kharif season. It requires well-drained black soils and moderate rainfall.",3800);
        CropInfo Maize_SorghumInfo = new CropInfo("June - July", "September - October", 120, 32, 20, 80, 30,"Maize is a cereal crop used for food, fodder, and industrial uses. It thrives in deep black soil with good drainage.",2000);
        CropInfo Paddy_SorghumInfo = new CropInfo("June - July", "October - November", 150, 30, 20, 90, 40,"Paddy (rice) is a water-intensive crop grown in kharif, requiring high humidity and standing water during early stages.",2300);
        CropInfo Wheat_SorghumInfo = new CropInfo("November", "March - April", 150, 25, 10, 70, 50,"Wheat is a rabi cereal crop requiring cool weather and alluvial to mixed soils, sown post-monsoon and harvested in spring.",2200);

        Map<String, CropInfo> BangaloreCrops = new HashMap<>();
        BangaloreCrops.put("Groundnut", GroundnutInfo);

        Map<String, CropInfo> BijapurCrops = new HashMap<>();
        BijapurCrops.put("Rabi Sorghum", Rabi_SorghumInfo);

        Map<String, CropInfo> RaichurCrops = new HashMap<>();
        RaichurCrops.put("Redgram", Redgram_SorghumInfo);

        Map<String, CropInfo> SolapurCrops = new HashMap<>();
        SolapurCrops.put("Rabi SorghumM", Rabi_SorghumM_Info);

        Map<String, CropInfo> AkolaCrops = new HashMap<>();
        AkolaCrops.put("Soybean", SoybeanInfo);

        Map<String, CropInfo> NashikCrops = new HashMap<>();
        NashikCrops.put("Pearl", Pearl_SorghumInfo);

        Map<String, CropInfo> BhopalCrops = new HashMap<>();
        BhopalCrops.put("Soybean", Soybean_SorghumInfo);

        Map<String, CropInfo> IndoreCrops = new HashMap<>();
        IndoreCrops.put("Maize", Maize_SorghumInfo);

        Map<String, CropInfo> JabalpurCrops = new HashMap<>();
        JabalpurCrops.put("Paddy", Paddy_SorghumInfo);

        Map<String, CropInfo> GwaliorCrops = new HashMap<>();
        GwaliorCrops.put("Wheat", Wheat_SorghumInfo);

        Map<String, Map<String, CropInfo>> karnatakaCities = new HashMap<>();
        karnatakaCities.put("Bangalore", BangaloreCrops);
        karnatakaCities.put("Bijapur", BijapurCrops);
        karnatakaCities.put("Raichur", RaichurCrops);

        Map<String, Map<String, CropInfo>> maharashtraCities = new HashMap<>();
        maharashtraCities.put("Solapur", SolapurCrops);
        maharashtraCities.put("Akola", AkolaCrops);
        maharashtraCities.put("Nashik", NashikCrops);

        Map<String, Map<String, CropInfo>> madhyaCities = new HashMap<>();
        madhyaCities.put("Bhopal", BhopalCrops);
        madhyaCities.put("Indore", IndoreCrops);
        madhyaCities.put("Jabalpur", JabalpurCrops);
        madhyaCities.put("Gwalior", GwaliorCrops);

        cropData.put("Karnataka", karnatakaCities);
        cropData.put("Maharashtra", maharashtraCities);
        cropData.put("Madhya Pradesh", madhyaCities);
    }

    // Save selection permanently
    public static void setSelection(Context context, String state, String city, String crop) {
        selectedState = state;
        selectedCity = city;
        selectedCrop = crop;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_STATE, state);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_CROP, crop);
        editor.apply();
    }

    // Load saved selection
    public static void loadSelection(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        selectedState = prefs.getString(KEY_STATE, null);
        selectedCity = prefs.getString(KEY_CITY, null);
        selectedCrop = prefs.getString(KEY_CROP, null);
    }

    // Check if selection exists
    public static boolean isSelectionSaved() {
        return selectedState != null && selectedCity != null && selectedCrop != null;
    }

    // Getters
    public static String getSelectedState() {
        return selectedState;
    }

    public static String getSelectedCity() {
        return selectedCity;
    }

    public static String getSelectedCrop() {
        return selectedCrop;
    }

    public static CropInfo getSelectedCropInfo() {
        if (selectedState == null || selectedCity == null || selectedCrop == null) {
            return null;
        }
        Map<String, Map<String, CropInfo>> cityMap = cropData.get(selectedState);
        if (cityMap == null) return null;

        Map<String, CropInfo> cropMap = cityMap.get(selectedCity);
        if (cropMap == null) return null;

        return cropMap.get(selectedCrop);
    }

    // Dynamic selection data
    public static String[] getAllStates() {
        return cropData.keySet().toArray(new String[0]);
    }

    public static String[] getCitiesByState(String state) {
        Map<String, Map<String, CropInfo>> cities = cropData.get(state);
        if (cities == null) return new String[0];
        return cities.keySet().toArray(new String[0]);
    }

    public static String[] getCropsByCity(String state, String city) {
        Map<String, Map<String, CropInfo>> cities = cropData.get(state);
        if (cities == null) return new String[0];

        Map<String, CropInfo> crops = cities.get(city);
        if (crops == null) return new String[0];

        return crops.keySet().toArray(new String[0]);
    }
}
