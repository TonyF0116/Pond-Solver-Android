package com.tony.pond_solver_android;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    public void print_map(String map) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 12; j += 2) {
                System.out.print(map.substring(12 * i + j, 12 * i + j + 2) + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void test_get_block_indices() {
        Solver solver = new Solver();
        String string_map = "000102030405000102030405000102030405000102030405000102030405000102030405";

        ArrayList<Integer> result = new ArrayList<>();
        result.add(0);
        result.add(6);
        result.add(12);
        result.add(18);
        result.add(24);
        result.add(30);

        assert (solver.get_block_indices(string_map, "00").equals(result));
    }

    @Test
    public void test_blocks_to_map() {
        Solver solver = new Solver();

        ArrayList<Block> blocks = new ArrayList<>();

        blocks.add(new Block(0, "empty", "empty", 1, 1, 1, 1));
        blocks.add(new Block(1, "Red", "Horizontal", 2, 1, 2, 2));

        print_map(solver.blocks_to_map(blocks));
        System.out.println(solver.blocks_to_map(blocks));
    }

    @Test
    public void test_map_to_blocks() {
        Solver solver = new Solver();

        String one_block = "000000000000000000000000000101000000000000000000000000000000000000000000";
        ArrayList<Block> result = solver.map_to_blocks(one_block);

        for (Block block : result) {
            block.print_block();
        }
    }

    @Test
    public void test_get_possible_moves() {
        Solver solver = new Solver();

        ArrayList<Block> blocks = new ArrayList<>();

        blocks.add(new Block(0, "empty", "empty", 1, 1, 1, 1));
        blocks.add(new Block(1, "Red", "Horizontal", 2, 1, 2, 2));

        ArrayList<String> result = solver.get_possible_moves(blocks);

        for (String move : result) {
            print_map(move);
            System.out.println();
        }
    }

    @Test
    public void test_solver() {
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(new Block(0, "", "", -1, -1, -1, -1));
        blocks.add(new Block(1, "Red", "Horizontal", 2, 0, 2, 1));
        blocks.add(new Block(2, "Yellow", "Horizontal", 0, 0, 0, 1));
        blocks.add(new Block(3, "Yellow", "Horizontal", 4, 2, 4, 3));
        blocks.add(new Block(4, "Yellow", "Horizontal", 5, 2, 5, 3));
        blocks.add(new Block(5, "Yellow", "Horizontal", 5, 4, 5, 5));
        blocks.add(new Block(6, "Yellow", "Horizontal", 3, 4, 3, 5));
        blocks.add(new Block(7, "Yellow", "Vertical", 0, 3, 1, 3));
        blocks.add(new Block(8, "Yellow", "Vertical", 0, 5, 1, 5));
        blocks.add(new Block(9, "Yellow", "Vertical", 2, 3, 3, 3));
        blocks.add(new Block(10, "Yellow", "Vertical", 4, 1, 5, 1));
        blocks.add(new Block(11, "Blue", "Vertical", 3, 0, 5, 0));
        blocks.add(new Block(12, "Blue", "Vertical", 0, 4, 2, 4));

        Solver solver = new Solver();
        ArrayList<ArrayList<Block>> solution = solver.solve(blocks);
        System.out.println(solution.size());
        for (ArrayList<Block> cur_blocks : solution) {
            System.out.println(solver.blocks_to_map(cur_blocks));
        }
    }
}