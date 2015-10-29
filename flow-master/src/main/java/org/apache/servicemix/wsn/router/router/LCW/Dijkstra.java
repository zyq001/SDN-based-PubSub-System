package org.apache.servicemix.wsn.router.router.LCW;

public class Dijkstra {
    static int M = 10000;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[][] weight1 = {//邻接矩阵
                {0, 3, 2000, 7, M},
                {3, 0, 4, 2, M},
                {M, 4, 0, 5, 4},
                {7, 2, 5, 0, 6},
                {M, M, 4, 6, 0}
        };

        int[][] weight2 = {
                {0, 10, M, 30, 100},
                {M, 0, 50, M, M},
                {M, M, 0, M, 10},
                {M, M, 20, 0, 60},
                {M, M, M, M, 0}
        };
        int start = 0;
//        int[] shortPath = Dijsktra(weight2, start);
//
//        for (int i = 0; i < shortPath.length; i++)
//            System.out.println("From " + start + " to " + i + ", the shortest distance is: " + shortPath[i]);

    }

    public static int[][] Dijsktra(int[][] weight, int start) {
        //接受一个有向图的权重矩阵，和一个起点编号start（从0编号，顶点存在数组中）
        //返回一个int[] 数组，表示从start到它的最短路径长度
        int n = weight.length;        //顶点个数
        int[] shortPath = new int[n];    //存放从start到其他各点的最短路径
        String[] path = new String[n]; //存放从start到其他各点的最短路径的字符串表示

//        int[] x = new int[10];

        int[][] path_1 = new int[n][1000];//！自己添加的一个二维数组，第一个下标表示目标交换机，第二个表示一路上的各个节点
        for (int i = 0; i < n; i++)
            for (int j = 0; j < 999; j++)
                path_1[i][j] = M;

        for (int i = 0; i < n; i++)
            path[i] = new String(start + "-->" + i);
        int[] visited = new int[n];   //标记当前该顶点的最短路径是否已经求出,1表示已求出

        //初始化，第一个顶点求出
        shortPath[start] = 0;
        visited[start] = 1;

        for (int count = 1; count <= n - 1; count++)  //要加入n-1个顶点
        {
            int k = -1;    //选出一个距离初始顶点start最近的未标记顶点
            int dmin = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (visited[i] == 0 && weight[start][i] < dmin) {
                    dmin = weight[start][i];
                    k = i;
                }
            }
            // System.out.println("k="+k);

            //将新选出的顶点标记为已求出最短路径，且到start的最短路径就是dmin
            shortPath[k] = dmin;
            visited[k] = 1;

            //以k为中间点，修正从start到未访问各点的距离
            for (int i = 0; i < n; i++) {
                if (visited[i] == 0 && weight[start][k] + weight[k][i] < weight[start][i]) {
                    weight[start][i] = weight[start][k] + weight[k][i];
                    path[i] = path[k] + "-->" + i;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            System.out.println("From " + start + " to " + i + ", the shortest path is: " + path[i]);
            String[] tmp = path[i].split("-->");
            for (int j = 0; j < tmp.length; j++) {
                path_1[i][j] = tmp[j].getBytes()[0] - 48;
            }
            System.out.println(tmp[0]);
        }
        System.out.println("=====================================");

//        return shortPath;
        return path_1;
    }
}