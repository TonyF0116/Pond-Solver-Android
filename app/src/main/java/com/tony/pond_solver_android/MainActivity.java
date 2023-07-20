package com.tony.pond_solver_android;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.View;
import android.view.DragEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final double scaling_factor = 2.5;
    private final ImageView[] draggable_block_views = new ImageView[5];
    private final ImageView[] block_views = new ImageView[19];
    private final ConstraintLayout.LayoutParams[] block_layouts = new ConstraintLayout.LayoutParams[19];
    private final ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<ArrayList<Block>> solution;
    private int cur_step = 0;
    private int total_steps = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blocks.add(new Block(0, "", "", -1, -1, -1, -1));

        initialize_input_page();
    }

    // Button functions
    public void undo(View v) {
        int index = blocks.size() - 1;
        if (index < 1) {
            return;
        }
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.removeView(block_views[index]);
        block_views[index] = null;
        block_layouts[index] = null;
        blocks.remove(index);

        if (blocks.size() == 1) {
            draggable_block_views[0].setVisibility(View.VISIBLE);
            draggable_block_views[1].setVisibility(View.GONE);
            draggable_block_views[2].setVisibility(View.GONE);
            draggable_block_views[3].setVisibility(View.GONE);
            draggable_block_views[4].setVisibility(View.GONE);
        }
    }

    public void solve(View v) {
        Solver solver = new Solver();
        solution = solver.solve(blocks);
        total_steps = solution.size();
        TextView step_count = findViewById(R.id.step_count);
        if (total_steps == 0) {
            step_count.setText(R.string.string_unsolvable);
        } else {
            step_count.setText(String.format(Locale.getDefault(),
                    "Cur step: %d, Total steps: %d", cur_step, total_steps));
        }

        for (ImageView cur_view : draggable_block_views) {
            cur_view.setVisibility(View.GONE);
        }
        findViewById(R.id.prev_button).setVisibility(View.VISIBLE);
        findViewById(R.id.next_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.undo_button).setVisibility(View.GONE);
        findViewById(R.id.solve_button).setVisibility(View.GONE);
    }

    public void prev_step(View v) {
        if (total_steps == 0) {
            return;
        }
        if (cur_step == 0) {
            cur_step = total_steps - 1;
        } else {
            cur_step--;
        }
        move();
    }

    public void next_step(View v) {
        if (total_steps == 0) {
            return;
        }
        cur_step = (cur_step + 1) % total_steps;
        move();
    }


    public void back(View v) {
        if (blocks.size() == 1) {
            draggable_block_views[0].setVisibility(View.VISIBLE);
        } else {
            draggable_block_views[1].setVisibility(View.VISIBLE);
            draggable_block_views[2].setVisibility(View.VISIBLE);
            draggable_block_views[3].setVisibility(View.VISIBLE);
            draggable_block_views[4].setVisibility(View.VISIBLE);
        }
        TextView textView = findViewById(R.id.step_count);
        textView.setText(R.string.string_text_view_initial);

        findViewById(R.id.prev_button).setVisibility(View.GONE);
        findViewById(R.id.next_button).setVisibility(View.GONE);
        findViewById(R.id.back_button).setVisibility(View.GONE);
        findViewById(R.id.undo_button).setVisibility(View.VISIBLE);
        findViewById(R.id.solve_button).setVisibility(View.VISIBLE);
    }

    // Initialize the input page, draw the canvas and add draggable blocks
    public void initialize_input_page() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

        // Draw the canvas
        ImageView canvas = new ImageView(this);
        canvas.setImageResource(R.drawable.canvas);
        ConstraintLayout.LayoutParams canvas_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        canvas_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        canvas_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        canvas_layout.leftMargin = 0;
        canvas_layout.topMargin = 0;
        int size = (int) (400 * scaling_factor);
        canvas_layout.width = size;
        canvas_layout.height = size;
        canvas.setLayoutParams(canvas_layout);
        constraintLayout.addView(canvas);

        // Set drag_drop listener for canvas
        View.OnDragListener canvas_drag_drop_listener = (v, event) -> {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                case DragEvent.ACTION_DROP:
                    add_block((String) event.getClipDescription().getLabel(), event.getX(), event.getY());
                    return true;
            }
            return false;
        };
        canvas.setOnDragListener(canvas_drag_drop_listener);


        // Initialize the draggable blocks
        // red block
        ImageView red_draggable = new ImageView(this);
        red_draggable.setImageResource(R.drawable.red);
        ConstraintLayout.LayoutParams red_draggable_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        red_draggable_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        red_draggable_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        red_draggable_layout.leftMargin = 200;
        red_draggable_layout.topMargin = 1100;
        red_draggable_layout.width = (int) (100 * scaling_factor);
        red_draggable_layout.height = (int) (50 * scaling_factor);
        red_draggable.setLayoutParams(red_draggable_layout);
        constraintLayout.addView(red_draggable);

        red_draggable.setOnLongClickListener(v -> {
            ClipData dragData = new ClipData("red",
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item("red"));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(red_draggable);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });

        // yellow_horizontal block
        ImageView yellow_horizontal_draggable = new ImageView(this);
        yellow_horizontal_draggable.setImageResource(R.drawable.yellow_horizontal);
        ConstraintLayout.LayoutParams yellow_horizontal_draggable_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        yellow_horizontal_draggable_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        yellow_horizontal_draggable_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        yellow_horizontal_draggable_layout.leftMargin = 200;
        yellow_horizontal_draggable_layout.topMargin = 1300;
        yellow_horizontal_draggable_layout.width = (int) (100 * scaling_factor);
        yellow_horizontal_draggable_layout.height = (int) (50 * scaling_factor);
        yellow_horizontal_draggable.setLayoutParams(yellow_horizontal_draggable_layout);
        constraintLayout.addView(yellow_horizontal_draggable);

        yellow_horizontal_draggable.setOnLongClickListener(v -> {
            ClipData dragData = new ClipData("yellow_horizontal",
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item("yellow_horizontal"));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(yellow_horizontal_draggable);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });
        yellow_horizontal_draggable.setVisibility(View.GONE);

        // yellow_vertical block
        ImageView yellow_vertical_draggable = new ImageView(this);
        yellow_vertical_draggable.setImageResource(R.drawable.yellow_vertical);
        ConstraintLayout.LayoutParams yellow_vertical_draggable_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        yellow_vertical_draggable_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        yellow_vertical_draggable_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        yellow_vertical_draggable_layout.leftMargin = 500;
        yellow_vertical_draggable_layout.topMargin = 1100;
        yellow_vertical_draggable_layout.width = (int) (50 * scaling_factor);
        yellow_vertical_draggable_layout.height = (int) (100 * scaling_factor);
        yellow_vertical_draggable.setLayoutParams(yellow_vertical_draggable_layout);
        constraintLayout.addView(yellow_vertical_draggable);

        yellow_vertical_draggable.setOnLongClickListener(v -> {
            ClipData dragData = new ClipData("yellow_vertical",
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item("yellow_vertical"));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(yellow_vertical_draggable);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });
        yellow_vertical_draggable.setVisibility(View.GONE);

        // blue_horizontal block
        ImageView blue_horizontal_draggable = new ImageView(this);
        blue_horizontal_draggable.setImageResource(R.drawable.blue_horizontal);
        ConstraintLayout.LayoutParams blue_horizontal_draggable_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        blue_horizontal_draggable_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        blue_horizontal_draggable_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        blue_horizontal_draggable_layout.leftMargin = 200;
        blue_horizontal_draggable_layout.topMargin = 1500;
        blue_horizontal_draggable_layout.width = (int) (150 * scaling_factor);
        blue_horizontal_draggable_layout.height = (int) (50 * scaling_factor);
        blue_horizontal_draggable.setLayoutParams(blue_horizontal_draggable_layout);
        constraintLayout.addView(blue_horizontal_draggable);

        blue_horizontal_draggable.setOnLongClickListener(v -> {
            ClipData dragData = new ClipData("blue_horizontal",
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item("blue_horizontal"));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(blue_horizontal_draggable);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });
        blue_horizontal_draggable.setVisibility(View.GONE);

        // blue_vertical block
        ImageView blue_vertical_draggable = new ImageView(this);
        blue_vertical_draggable.setImageResource(R.drawable.blue_vertical);
        ConstraintLayout.LayoutParams blue_vertical_draggable_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        blue_vertical_draggable_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        blue_vertical_draggable_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        blue_vertical_draggable_layout.leftMargin = 700;
        blue_vertical_draggable_layout.topMargin = 1100;
        blue_vertical_draggable_layout.width = (int) (50 * scaling_factor);
        blue_vertical_draggable_layout.height = (int) (150 * scaling_factor);
        blue_vertical_draggable.setLayoutParams(blue_vertical_draggable_layout);
        constraintLayout.addView(blue_vertical_draggable);

        blue_vertical_draggable.setOnLongClickListener(v -> {
            ClipData dragData = new ClipData("blue_vertical",
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item("blue_vertical"));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(blue_vertical_draggable);
            v.startDragAndDrop(dragData, shadowBuilder, null, 0);
            return true;
        });
        blue_vertical_draggable.setVisibility(View.GONE);

        draggable_block_views[0] = red_draggable;
        draggable_block_views[1] = yellow_horizontal_draggable;
        draggable_block_views[2] = yellow_vertical_draggable;
        draggable_block_views[3] = blue_horizontal_draggable;
        draggable_block_views[4] = blue_vertical_draggable;
    }

    // Add new block and store reference to the block, image view, and layout
    public void add_block(String block_type, float x_dp, float y_dp) {
//        System.out.printf("Block: %s, x_dp: %f, y_dp: %f\n", block_type, x_dp, y_dp);
        TextView step_count = findViewById(R.id.step_count);
        step_count.setText(R.string.string_add_block_fail);
        step_count.setText(R.string.string_add_block_success);

        int x = 0;
        int y = 0;
        int index = blocks.size();
        Block cur_block;

        switch (block_type) {
            case "red":
                while (x_dp >= 210) {
                    if (x_dp < 310) {
                        break;
                    } else {
                        x_dp -= 132.5;
                        y++;
                        if (x_dp < 210) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                while (y_dp >= 200) {
                    if (y_dp < 290) {
                        break;
                    } else {
                        y_dp -= 132.5;
                        x++;
                        if (y_dp < 200) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                if (x != 2) {
                    step_count.setText(R.string.string_add_block_fail);
                    return;
                }
                cur_block = new Block(index, "Red", "Horizontal", x, y, x, y + 1);
                blocks.add(cur_block);
//                System.out.printf("Block: %s, x: %d, y: %d\n", block_type, x, y);
                break;
            case "yellow_horizontal":
                while (x_dp >= 210) {
                    if (x_dp < 310) {
                        break;
                    } else {
                        x_dp -= 132.5;
                        y++;
                        if (x_dp < 210) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                while (y_dp >= 200) {
                    if (y_dp < 290) {
                        break;
                    } else {
                        y_dp -= 132.5;
                        x++;
                        if (y_dp < 200) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                cur_block = new Block(index, "Yellow", "Horizontal", x, y, x, y + 1);
                blocks.add(cur_block);
//                System.out.printf("Block: %s, x: %d, y: %d\n", block_type, x, y);
                break;
            case "yellow_vertical":
                while (x_dp >= 150) {
                    if (x_dp < 240) {
                        break;
                    } else {
                        x_dp -= 132.5;
                        y++;
                        if (x_dp < 150) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                while (y_dp >= 200) {
                    if (y_dp < 300) {
                        break;
                    } else {
                        y_dp -= 132.5;
                        x++;
                        if (y_dp < 200) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                cur_block = new Block(index, "Yellow", "Vertical", x, y, x + 1, y);
                blocks.add(cur_block);
//                System.out.printf("Block: %s, x: %d, y: %d\n", block_type, x, y);
                break;
            case "blue_horizontal":
                while (x_dp >= 270) {
                    if (x_dp < 380) {
                        break;
                    } else {
                        x_dp -= 132.5;
                        y++;
                        if (x_dp < 270) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                while (y_dp >= 200) {
                    if (y_dp < 290) {
                        break;
                    } else {
                        y_dp -= 132.5;
                        x++;
                        if (y_dp < 200) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                cur_block = new Block(index, "Blue", "Horizontal", x, y, x, y + 2);
                blocks.add(cur_block);
//                System.out.printf("Block: %s, x: %d, y: %d\n", block_type, x, y);
                break;
            case "blue_vertical":
                while (x_dp >= 150) {
                    if (x_dp < 240) {
                        break;
                    } else {
                        x_dp -= 132.5;
                        y++;
                        if (x_dp < 150) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                while (y_dp >= 195) {
                    if (y_dp < 305) {
                        break;
                    } else {
                        y_dp -= 132.5;
                        x++;
                        if (y_dp < 195) {
                            step_count.setText(R.string.string_add_block_fail);
                            return;
                        }
                    }
                }
                cur_block = new Block(index, "Blue", "Vertical", x, y, x + 2, y);
                blocks.add(cur_block);
//                System.out.printf("Block: %s, x: %d, y: %d\n", block_type, x, y);
                break;
            default:
                step_count.setText(R.string.string_invalid_block);
                return;
        }

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);


        ImageView tmp = new ImageView(this);
        ConstraintLayout.LayoutParams tmp_layout = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        int leftMargin = (int) (54 * scaling_factor + cur_block.y1 * 53 * scaling_factor);
        int topMargin = (int) (67 * scaling_factor + cur_block.x1 * 53 * scaling_factor);
        tmp_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        tmp_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        tmp_layout.leftMargin = leftMargin;
        tmp_layout.topMargin = topMargin;
        if (cur_block.orientation.equals("Horizontal")) {
            if (cur_block.color.equals("Red")) {
                tmp.setImageResource(R.drawable.red);
                tmp_layout.width = (int) (100 * scaling_factor);
            } else if (cur_block.color.equals("Yellow")) {
                tmp.setImageResource(R.drawable.yellow_horizontal);
                tmp_layout.width = (int) (100 * scaling_factor);
            } else {
                tmp.setImageResource(R.drawable.blue_horizontal);
                tmp_layout.width = (int) (150 * scaling_factor);
            }
            tmp_layout.height = (int) (50 * scaling_factor);
        } else {
            if (cur_block.color.equals("Yellow")) {
                tmp.setImageResource(R.drawable.yellow_vertical);
                tmp_layout.height = (int) (100 * scaling_factor);
            } else {
                tmp.setImageResource(R.drawable.blue_vertical);
                tmp_layout.height = (int) (150 * scaling_factor);
            }
            tmp_layout.width = (int) (50 * scaling_factor);
        }
        tmp.setLayoutParams(tmp_layout);
        constraintLayout.addView(tmp);
        block_layouts[index] = tmp_layout;
        block_views[index] = tmp;


        if (blocks.size() == 2) {
            draggable_block_views[0].setVisibility(View.GONE);
            draggable_block_views[1].setVisibility(View.VISIBLE);
            draggable_block_views[2].setVisibility(View.VISIBLE);
            draggable_block_views[3].setVisibility(View.VISIBLE);
            draggable_block_views[4].setVisibility(View.VISIBLE);
        }
    }

    // Update block positions based on the block info of the current step
    public void move() {
        TextView step_count = findViewById(R.id.step_count);
        step_count.setText(String.format(Locale.getDefault(),
                "Cur step: %d, Total steps: %d", cur_step, total_steps));

        ArrayList<Block> cur_step_blocks = solution.get(cur_step);

        for (int i = 0; i < cur_step_blocks.size(); i++) {
            if (i != 0) {
                Block cur_block = cur_step_blocks.get(i);
                ConstraintLayout.LayoutParams tmp_layout = block_layouts[i];
                int leftMargin = (int) (54 * scaling_factor + cur_block.y1 * 53 * scaling_factor);
                int topMargin = (int) (67 * scaling_factor + cur_block.x1 * 53 * scaling_factor);
                tmp_layout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                tmp_layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                tmp_layout.leftMargin = leftMargin;
                tmp_layout.topMargin = topMargin;

                block_views[i].setLayoutParams(tmp_layout);
            }
        }
        findViewById(R.id.constraintLayout).requestLayout();
    }


}