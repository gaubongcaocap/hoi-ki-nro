package map;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
public class MapManager {
    private static final String EFF_MAP_PATH = "data/map/eff_map/";
    private static final Map<Integer, List<EffectMap>> EFF_MAP_CACHE = new HashMap<>();

    // Load tất cả hiệu ứng map khi server khởi động
    public static void loadAllEffMaps() {
        File folder = new File(EFF_MAP_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Thư mục không tồn tại: " + EFF_MAP_PATH);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.startsWith("effmap_") && name.endsWith(".ini"));
        Set<Integer> loadedMaps = new HashSet<>();

        if (files != null) {
            for (File file : files) {
                try {
                    int mapId = extractMapId(file.getName());
                    List<EffectMap> effectList = loadEffMapFromFile(file);

   
                    ensureBeff(effectList);

                    EFF_MAP_CACHE.put(mapId, effectList);
                    loadedMaps.add(mapId);
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi khi lấy ID từ file: " + file.getName());
                } catch (IOException e) {
                    System.err.println("Lỗi khi đọc file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        ensureAllMapsHaveBeff15(loadedMaps);
    }

    // Lấy hiệu ứng của map từ cache
    public static List<EffectMap> getEffMap(int mapId) {
        return EFF_MAP_CACHE.getOrDefault(mapId, Collections.emptyList());
    }

    // Đọc file hiệu ứng và parse JSON
    private static List<EffectMap> loadEffMapFromFile(File file) throws IOException {
        List<EffectMap> effectList = new ArrayList<>();

        String content = Files.readString(Path.of(file.getAbsolutePath()));
        JSONArray dataArray = (JSONArray) JSONValue.parse(content);
        if (dataArray == null) {
            return effectList;
        }

        for (Object obj : dataArray) {
            if (!(obj instanceof JSONArray dataItem) || dataItem.size() < 2) {
                continue;
            }

            EffectMap em = new EffectMap();
            em.setKey((String) dataItem.get(0));
            em.setValue((String) dataItem.get(1));
            effectList.add(em);
        }

        return effectList;
    }

    // Hàm đảm bảo ["beff", "15"] luôn tồn tại trong danh sách hiệu ứng
    private static void ensureBeff(List<EffectMap> effectList) {
        for (EffectMap em : effectList) {
            if ("beff".equals(em.getKey()) && "18".equals(em.getValue())) {
                return; 
            }
        }
        // Nếu chưa có, thêm mới
        EffectMap beff15 = new EffectMap();
        beff15.setKey("beff");
        beff15.setValue("15");
        effectList.add(beff15);
    }

    // Hàm đảm bảo mọi map đều có ["beff", "15"], kể cả khi không có file
    private static void ensureAllMapsHaveBeff15(Set<Integer> loadedMaps) {
        int totalMaps = 200; // Giả sử có tổng cộng 100 map (có thể thay đổi số này)

        for (int mapId = 1; mapId <= totalMaps; mapId++) {
            if (!loadedMaps.contains(mapId)) {
                List<EffectMap> effectList = new ArrayList<>();
                ensureBeff(effectList);
                EFF_MAP_CACHE.put(mapId, effectList);
//                System.out.println("Tạo hiệu ứng mặc định cho map " + mapId);
            }
        }
    }

    // Hàm tách số từ tên file
    private static int extractMapId(String fileName) throws NumberFormatException {
        return Integer.parseInt(fileName.replaceAll("\\D+", ""));
    }
}