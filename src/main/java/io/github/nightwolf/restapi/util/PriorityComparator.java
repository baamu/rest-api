package io.github.nightwolf.restapi.util;

import io.github.nightwolf.restapi.dto.DownloadDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * @author oshan
 */
public class PriorityComparator implements Comparator<DownloadDTO> {

    private SimpleDateFormat datePattern = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public int compare(DownloadDTO o1, DownloadDTO o2) {
        try {
            return datePattern.parse(o1.getAddedDate()).compareTo(datePattern.parse(o2.getAddedDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
