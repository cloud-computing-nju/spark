package adapter;

import model.DataModel;
import org.apache.kafka.common.serialization.Deserializer;
import utils.BeanUtils;

import java.io.IOException;
import java.util.Map;

public class DataModelDecoder implements Deserializer<DataModel> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public DataModel deserialize(String s, byte[] bytes) {
        try {
            return (DataModel) BeanUtils.deserialize(bytes);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
