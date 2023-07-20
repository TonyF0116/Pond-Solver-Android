package com.tony.pond_solver_android;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class Solver {
    /*
      In all member functions, Arraylists instead of arrays should be used to store the blocks,
      which contain specific information for each block.
      string_map and blocks are the only two format that can be used as parameters.
     */


    /**
     * Get the indices of the grids which are occupied by the block of the given block index
     *
     * @param string_map  the String map
     * @param block_index the block index to find
     * @return return an arraylist containing the found indices
     */
    public ArrayList<Integer> get_block_indices(String string_map, String block_index) {
        String[] array_map = new String[36];
        for (int i = 0; i < string_map.length() / 2; i++) {
            array_map[i] = string_map.substring(2 * i, 2 * i + 2);
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < array_map.length; i++) {
            if (array_map[i].equals(block_index)) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Change from an arraylist of blocks to string map
     *
     * @param blocks arraylist of blocks, containing information of each block
     * @return return the string map
     */
    public String blocks_to_map(ArrayList<Block> blocks) {
        String[] result = new String[36];
        Arrays.fill(result, "00");
        for (int i = 1; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block.orientation.equals("Horizontal")) {
                for (int j = block.y1; j <= block.y2; j++) {
                    if (block.index < 10) {
                        result[block.x1 * 6 + j] = "0" + block.index;
                    } else {
                        result[block.x1 * 6 + j] = "" + block.index;
                    }
                }
            } else if (block.orientation.equals("Vertical")) {
                for (int j = block.x1; j <= block.x2; j++) {
                    if (block.index < 10) {
                        result[6 * j + block.y1] = "0" + block.index;
                    } else {
                        result[6 * j + block.y1] = "" + block.index;
                    }
                }
            } else {
                throw new RuntimeException("Invalid Orientation");
            }
        }
        StringBuilder combinedString = new StringBuilder();
        for (String element : result) {
            combinedString.append(element);
        }
        return combinedString.toString();
    }

    /**
     * Change from string map to an arraylist of blocks
     *
     * @param string_map the map in string format
     * @return arraylist of blocks, containing information of each block
     */
    public ArrayList<Block> map_to_blocks(String string_map) {
        ArrayList<Block> result = new ArrayList<>();
        result.add(new Block(0, "Empty", "Empty",
                -1, -1, -1, -1));

        for (int i = 1; i <= 18; i++) {
            ArrayList<Integer> indices;
            if (i < 10) {
                indices = get_block_indices(string_map, "0" + i);
            } else {
                indices = get_block_indices(string_map, "" + i);
            }
            if (indices.isEmpty()) {
                break;
            }

            Block block = new Block(0, "", "", -1, -1, -1, -1);
            if (indices.size() == 2) {
                if (i == 1) {
                    block.index = 1;
                    block.color = "Red";
                    block.orientation = "Horizontal";
                    block.x1 = 2;
                    block.y1 = indices.get(0) - 12;
                    block.x2 = 2;
                    block.y2 = indices.get(1) - 12;
                } else {
                    if (indices.get(1) - indices.get(0) == 1) {
                        block.index = i;
                        block.color = "Yellow";
                        block.orientation = "Horizontal";
                        block.x1 = indices.get(0) / 6;
                        block.y1 = indices.get(0) % 6;
                        block.x2 = indices.get(1) / 6;
                        block.y2 = indices.get(1) % 6;
                    } else {
                        block.index = i;
                        block.color = "Yellow";
                        block.orientation = "Vertical";
                        block.x1 = indices.get(0) / 6;
                        block.y1 = indices.get(0) % 6;
                        block.x2 = indices.get(1) / 6;
                        block.y2 = indices.get(1) % 6;
                    }
                }
            } else if (indices.size() == 3) {
                if (indices.get(1) - indices.get(0) == 1) {
                    block.index = i;
                    block.color = "Blue";
                    block.orientation = "Horizontal";
                    block.x1 = indices.get(0) / 6;
                    block.y1 = indices.get(0) % 6;
                    block.x2 = indices.get(2) / 6;
                    block.y2 = indices.get(2) % 6;
                } else {
                    block.index = i;
                    block.color = "Blue";
                    block.orientation = "Vertical";
                    block.x1 = indices.get(0) / 6;
                    block.y1 = indices.get(0) % 6;
                    block.x2 = indices.get(2) / 6;
                    block.y2 = indices.get(2) % 6;

                }
            } else {
                throw new RuntimeException("Invalid length");
            }
            result.add(block);
        }
        return result;
    }

    /**
     * Get the possible string maps which can be achieved within one move from the given map
     *
     * @param blocks the arraylist of all blocks on the canvas
     * @return an arraylist of possible string maps
     */
    public ArrayList<String> get_possible_moves(ArrayList<Block> blocks) {
        ArrayList<String> result = new ArrayList<>();

        String string_map = blocks_to_map(blocks);
        String[] initial_array_map = new String[36];
        for (int i = 0; i < string_map.length() / 2; i++) {
            initial_array_map[i] = string_map.substring(2 * i, 2 * i + 2);
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block block = blocks.get(i);

            int start = block.x1 * 6 + block.y1;
            int end = block.x2 * 6 + block.y2;
            if (block.orientation.equals("Horizontal")) {

                for (int j = start - 1; j >= block.x1 * 6; j--) {
                    if (initial_array_map[j].equals("00")) {
                        int displacement = start - j;
                        String[] cur_array_map = Arrays.copyOf(initial_array_map, 36);
                        if (block.index < 10) {
                            for (int k = start; k <= end; k++) {
                                cur_array_map[k] = "00";
                                cur_array_map[k - displacement] = "0" + block.index;
                            }
                        } else {
                            for (int k = start; k <= end; k++) {
                                cur_array_map[k] = "00";
                                cur_array_map[k - displacement] = "" + block.index;
                            }
                        }
                        StringBuilder combinedString = new StringBuilder();
                        for (String element : cur_array_map) {
                            combinedString.append(element);
                        }
                        result.add(combinedString.toString());
                    } else {
                        break;
                    }
                }

                for (int j = end + 1; j < block.x1 * 6 + 6; j++) {
                    if (initial_array_map[j].equals("00")) {
                        int displacement = j - end;
                        String[] cur_array_map = Arrays.copyOf(initial_array_map, 36);
                        if (block.index < 10) {
                            for (int k = end; k >= start; k--) {
                                cur_array_map[k] = "00";
                                cur_array_map[k + displacement] = "0" + block.index;
                            }
                        } else {
                            for (int k = end; k >= start; k--) {
                                cur_array_map[k] = "00";
                                cur_array_map[k + displacement] = "" + block.index;
                            }
                        }
                        StringBuilder combinedString = new StringBuilder();
                        for (String element : cur_array_map) {
                            combinedString.append(element);
                        }
                        result.add(combinedString.toString());
                    } else {
                        break;
                    }
                }
            } else {

                for (int j = start - 6; j >= 0; j -= 6) {
                    if (initial_array_map[j].equals("00")) {
                        int displacement = start - j;
                        String[] cur_array_map = Arrays.copyOf(initial_array_map, 36);
                        if (block.index < 10) {
                            for (int k = start; k <= end; k += 6) {
                                cur_array_map[k] = "00";
                                cur_array_map[k - displacement] = "0" + block.index;
                            }
                        } else {
                            for (int k = start; k <= end; k += 6) {
                                cur_array_map[k] = "00";
                                cur_array_map[k - displacement] = "" + block.index;
                            }
                        }
                        StringBuilder combinedString = new StringBuilder();
                        for (String element : cur_array_map) {
                            combinedString.append(element);
                        }
                        result.add(combinedString.toString());
                    } else {
                        break;
                    }
                }

                for (int j = end + 6; j < 36; j += 6) {
                    if (initial_array_map[j].equals("00")) {
                        int displacement = j - end;
                        String[] cur_array_map = Arrays.copyOf(initial_array_map, 36);
                        if (block.index < 10) {
                            for (int k = end; k >= start; k -= 6) {
                                cur_array_map[k] = "00";
                                cur_array_map[k + displacement] = "0" + block.index;
                            }
                        } else {
                            for (int k = end; k >= start; k -= 6) {
                                cur_array_map[k] = "00";
                                cur_array_map[k + displacement] = "" + block.index;
                            }
                        }
                        StringBuilder combinedString = new StringBuilder();
                        for (String element : cur_array_map) {
                            combinedString.append(element);
                        }
                        result.add(combinedString.toString());
                    } else {
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Check if the puzzle corresponding to the string map is solved
     *
     * @param string_map current string map
     * @return true if solved
     */
    public boolean solved(String string_map) {
        String[] array_map = new String[36];
        for (int i = 0; i < string_map.length() / 2; i++) {
            array_map[i] = string_map.substring(2 * i, 2 * i + 2);
        }

        for (int i = 17; i >= 12; i--) {
            if (array_map[i].equals("00")) {
                continue;
            } else if (array_map[i].equals("01")) {
                return true;
            } else {
                break;
            }
        }
        return false;
    }

    /**
     * Use BFS to solve the puzzle
     * <p>
     * added set is necessary, both for fasten solving speed and
     * for prevent one-grid moving from overwriting multi-grids moving in backtracking
     *
     * @param blocks arraylist of blocks
     * @return the step by step solution
     */
    public ArrayList<ArrayList<Block>> solve(ArrayList<Block> blocks) {
        String initial_string_map = blocks_to_map(blocks);
        Set<String> visited = new HashSet<>();
        Set<String> added = new HashSet<>();
        ArrayList<String> to_visit = new ArrayList<>();
        Map<String, String> back_tracking = new HashMap<>();

        to_visit.add(initial_string_map);
        added.add(initial_string_map);
        String last_visited = initial_string_map;
        boolean solution_generated = false;

        while (!to_visit.isEmpty()) {
            String cur_string_map = to_visit.remove(0);
            if (visited.contains(cur_string_map)) {
                continue;
            }
            ArrayList<Block> cur_blocks = map_to_blocks(cur_string_map);
            visited.add(cur_string_map);

            if (solved(cur_string_map)) {
                last_visited = cur_string_map;
                solution_generated = true;
                break;
            }

            ArrayList<String> possible_moves = get_possible_moves(cur_blocks);
            for (String move : possible_moves) {
                if (visited.contains(move) || added.contains(move)) {
                    continue;
                } else {
                    to_visit.add((move));
                    added.add(move);
                    back_tracking.put(move, cur_string_map);
                }
            }
        }

        ArrayList<String> result = new ArrayList<>();
        ArrayList<ArrayList<Block>> result_blocks = new ArrayList<>();

        if (!solution_generated) {
            return result_blocks;
        }

        result.add(last_visited);
        int index = 0;

        while (!Objects.equals(back_tracking.getOrDefault(result.get(index), "End"), "End")) {
            result.add(back_tracking.get(result.get(index)));
            index++;
        }

        for (int i = result.size() - 1; i >= 0; i--) {
            result_blocks.add(map_to_blocks(result.get(i)));
        }
        return result_blocks;
    }

}
