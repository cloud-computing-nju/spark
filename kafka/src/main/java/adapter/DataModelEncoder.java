package adapter;

import model.DataModel;
import org.apache.kafka.common.serialization.Serializer;
import utils.BeanUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 自定义对象序列化器,可在配置producer中使用
 */
public class DataModelEncoder implements Serializer<DataModel> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, DataModel dataModel) {
        try {
            return BeanUtils.serialize(dataModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
