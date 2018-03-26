package com.wayj.inputbinder.example.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GridPage<T>  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当前页数
     */
    private long page = 0;
    /**
     * 总页数
     */
    private long total = 0;
    /**
     * 总条数
     */
    private long records = 0;
    /**
     * 数据集合
     */
    private List<T> rows = new ArrayList<>();

    public GridPage(PageInfo<T> pageInfo) {
        setPage(pageInfo.getCurrentPage());
        setRecords(pageInfo.getTotalRowSize());
        setTotal(pageInfo.getPageNum());
        List<T> list = pageInfo.getResult();
        setRows(list);
    }

    public GridPage(List<T> list) {
        setRecords(list.size());
        setRows(list);
    }

    public GridPage() {
        super();
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public void addAll(GridPage<T> gridPage) {
        page = gridPage.getPage();
        total = gridPage.getTotal();
        records = gridPage.getRecords();
        rows.addAll(gridPage.getRows());
    }

    public void clear() {
        page = 0;
        total = 0;
        records = 0;
        rows = new ArrayList<>();
    }

    public void add(T object) {
        rows.add(object);
        records = records + 1;
    }

    public void addAll(List<T> object) {
        rows.addAll(object);
        records = records + object.size();
    }

    public void add(int location, T object) {
        rows.add(location, object);
        records = records + 1;
    }


    public class PageInfo<T> implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private int perPageSize = 10;

        private int currentPage = 1;

        private List<T> result;

        private long totalRowSize = 0;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageNum() {
            if (totalRowSize % perPageSize > 0) {
                return (int) (totalRowSize / perPageSize) + 1;
            } else {
                return (int) (totalRowSize / perPageSize);
            }
        }

        public List<T> getResult() {
            return result;
        }

        public void setResult(List<T> result) {
            this.result = result;
        }

        public long getTotalRowSize() {
            return totalRowSize;
        }

        public void setTotalRowSize(long totalRowSize) {
            this.totalRowSize = totalRowSize;
        }

        public boolean haveNextPage() {
            return (currentPage - 1) * perPageSize + result.size() < totalRowSize;
        }

        public boolean havePreviousPage() {
            return currentPage > 1;
        }

        public int getPerPageSize() {
            return perPageSize;
        }

        public void setPerPageSize(int perPageSize) {
            this.perPageSize = perPageSize;
        }
    }
}
