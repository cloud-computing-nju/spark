package preprocessor;

import model.DataModel;

public class DataModelPreprocessor implements Preprocessor {
    @Override
    public String preprocess(DataModel dataModel) {
        String[] tags = dataModel.getTags();
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(tag);
            sb.append(" ");
        }
        String res = sb.toString();
        return res;
    }
}
