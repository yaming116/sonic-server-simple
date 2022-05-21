package org.cloud.sonic.simple.models.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页对象
 *
 * @author JayWenStar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPage<T> implements Serializable {

    /**
     * 页大小
     */
    private long size;

    /**
     * 页内容
     */
    private List<T> content;

    /**
     * 当前页码
     */
    private long number;

    /**
     * 页内容总个数
     */
    private long totalElements;

    /**
     * 总页数
     */
    private long totalPages;

    public static <T> CommentPage<T> convertFrom(Page<T> page) {
        return new CommentPage<>(
                page.getSize(), page.getRecords(), page.getCurrent() - 1, page.getTotal(), page.getPages()
        );
    }

    /**
     * Page的数据会被content替代
     */
    public static <T> CommentPage<T> convertFrom(Page<?> page, List<T> content) {
        return new CommentPage<>(
                page.getSize(), content, page.getCurrent() - 1, page.getTotal(), page.getPages()
        );
    }


    public static <T> CommentPage<T> emptyPage() {
        return new CommentPage<>(0, new ArrayList<>(), 0, 0, 0);
    }


    /**
     * @param list 进行分页的list
     * @param pageNo 页码
     * @param pageSize 每页显示条数
     * @return 分页后数据
     */
    public static <T> List<T> listPaging(List<T> list, Integer pageNo, Integer pageSize){
        if(list == null){
            list = new ArrayList<T>();
        }
        if(pageNo == null){
            pageNo = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        if(pageNo <= 0){
            pageNo = 1;
        }

        int totalitems = list.size();
        List<T> pagingList = new ArrayList<T>();

        int totalNum = ((pageNo - 1) * pageSize) + pageSize > totalitems ? totalitems : ((pageNo - 1) * pageSize) + pageSize;
        for(int i = (pageNo-1)*pageSize; i < totalNum; i++) {
            pagingList.add(list.get(i));
        }
        return pagingList;
    }

}
