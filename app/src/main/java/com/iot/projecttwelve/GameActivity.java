package com.iot.projecttwelve;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class GameActivity extends Activity {

    int order = 0;  //1이면 선공 2면 후공
    int turn = 0; //소비 턴수를 계산
    int index = -1; //선택한 말의 보드상의 위치
    int [] pieces;  //말 // 0은 빈칸, 11은 타겟, 1자, 2후, 3상, 4장, 5왕, 6~10도 같은식
    int G_POW_CNT = 0;  //  녹색 포로수
    int R_POW_CNT = 0;  // 붉은색 포로수
    int [] G_POW_LIST;
    int [] R_POW_LIST;
    int RedVictoryChance = 0; //승리조건
    int GreenVictoryChance = 0; //승리조건
    int freedom = 0;
    int powNum = -1;


    int [][] board;   //게임판 [4][3]배열로 만들어진다. 저장되는 값은 해당 발판에 있는 말의 인덱스
    int [][] iff;       //피아식별
    int [][] targetLocation;    //타겟위치 3x3
    BitmapDrawable[] value;
    ImageView[] piece;
    ImageView[] select;
    ImageView[] target;
    ImageView[] G_POW;  //녹색의 포로
    ImageView[] R_POW;  //붉은색의 포로

    AlertDialog victorydialog;
    int selectedPiece = 0;  //선택된 피스가 있다.  1   없다.  0
    int selectedPieceValue = 0; //선택된 피스의 종류를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pieces = new int[11];   // 0은 빈칸, 1자, 2후, 3상, 4장, 5왕, 6~10도 같은식, 해당 값들의 현재 위치를 지정,
        initPieces();
        initTargetLocation();
        setPieces();
        for (int i = 0; i< 11; ++i)
        {
            pieces[i] = 0;
        }
        AlertDialog dialog = createDialogBox();
        dialog.show();


        //선공 후공을 정한다.
        //내턴이면 ~~~
        //상대턴이면 ~~
        //
        //규칙부터 정해봅시다.
        //먼저 자신의 말만 건드릴 수 있다.
        //즉 나는 빨간놈만 건드릴 수 있다. 판별은 어떻게 하는가.pieces값이 1~5인경우만 가능
        //전체 판을 배열로 저장한다. 즉 [4][3]배열을 갖는 보드를 만들어둔다.
        //해당보드는 그 칸에 있는 말의 인덱스를 저장한다.
        //인덱스가 1~5인 경우만 건드릴 수 있다.
        //각 말의 이동은 변경후 [0~3][0~2]인 경우만 이동가능하다.
        //인덱스가 1인 경우 [x][y]현재위치에서 [x-1][y]로밖에 이동할 수 없다.
        //x==0이면 인덱스를 2로 변경한다.
        //인덱스가 2인경우 [x][y]에서 [x-1][y-1], [x-1][y], [x-1][y+1], [x][y-1], [x][y+1], [x+1][y]로 이동가능
        //인덱스가 3인 경우 [x-1][y-1],[x-1][y+1],[x+1][y-1],[x+1][y+1]로 이동가능
        //인덱스가 4인경우 [x-1][y],[x][y-1],[x][y+1],[x+1][y]로 이동가능
        //인덱스가 5인경우 [x-1][y-1], [x-1][y], [x-1][y+1], [x][y-1], [x][y+1], [x+1][y-1],[x+1][y],[x+1][y+1]
        //해당 말을 선택할 경우, ((본인의 색을 변화))시킨다. 선택된 색으로,
        //이동가능한 칸에 타겟을 표시한다. 잡을 수 있는 적의 경우 타겟을 표시한다.
        //1. 아군 말만 선택 가능하게
        //2. 선택한 말의 상태를 변화시켜줌
        //3. 선택한 말이 움직일 수 있는 곳을 전부 표시
        //4. 선택한 말이 갈 수 있는 곳 중 아군이 있는 곳을 제외
        //
    }
    private void initTargetLocation()
    {
        targetLocation = new int[][] {
                {1,1,1},
                {1,0,1},
                {1,1,1}
        };
    }
    public void setIff() {
        for (int i = 0; i < 12; ++i)
        {
            iff[i/3][i%3] = 0;
            for (int j = 1; j <= 5; ++j)
            {
                if (board[i/3][i%3] == j)
                    iff[i/3][i%3] = 1;
            }
            for (int j = 6; j <= 10; ++j)
            {
                if (board[i/3][i%3] == j)
                    iff[i/3][i%3] = -1;
            }
        }
    }
    private void initPieces() {
        board = new int[][] {
                {9, 10, 8},
                {0, 6, 0},
                {0, 1, 0},
                {3, 5, 4}
        };
        iff = new int[][] {
                {-1, -1, -1},
                {0, -1, 0},
                {0, 1, 0},
                {1, 1, 1}
        };


        piece = new ImageView[12];
        piece[0] = (ImageView)findViewById(R.id.piece11);
        piece[1] = (ImageView)findViewById(R.id.piece12);
        piece[2] = (ImageView)findViewById(R.id.piece13);
        piece[3] = (ImageView)findViewById(R.id.piece21);
        piece[4] = (ImageView)findViewById(R.id.piece22);
        piece[5] = (ImageView)findViewById(R.id.piece23);
        piece[6] = (ImageView)findViewById(R.id.piece31);
        piece[7] = (ImageView)findViewById(R.id.piece32);
        piece[8] = (ImageView)findViewById(R.id.piece33);
        piece[9] = (ImageView)findViewById(R.id.piece41);
        piece[10] = (ImageView)findViewById(R.id.piece42);
        piece[11] = (ImageView)findViewById(R.id.piece43);

        select = new ImageView[12];
        select[0] = (ImageView)findViewById(R.id.select11);
        select[1] = (ImageView)findViewById(R.id.select12);
        select[2] = (ImageView)findViewById(R.id.select13);
        select[3] = (ImageView)findViewById(R.id.select21);
        select[4] = (ImageView)findViewById(R.id.select22);
        select[5] = (ImageView)findViewById(R.id.select23);
        select[6] = (ImageView)findViewById(R.id.select31);
        select[7] = (ImageView)findViewById(R.id.select32);
        select[8] = (ImageView)findViewById(R.id.select33);
        select[9] = (ImageView)findViewById(R.id.select41);
        select[10] = (ImageView)findViewById(R.id.select42);
        select[11] = (ImageView)findViewById(R.id.select43);

        target = new ImageView[12];
        target[0] = (ImageView)findViewById(R.id.target11);
        target[1] = (ImageView)findViewById(R.id.target12);
        target[2] = (ImageView)findViewById(R.id.target13);
        target[3] = (ImageView)findViewById(R.id.target21);
        target[4] = (ImageView)findViewById(R.id.target22);
        target[5] = (ImageView)findViewById(R.id.target23);
        target[6] = (ImageView)findViewById(R.id.target31);
        target[7] = (ImageView)findViewById(R.id.target32);
        target[8] = (ImageView)findViewById(R.id.target33);
        target[9] = (ImageView)findViewById(R.id.target41);
        target[10] = (ImageView)findViewById(R.id.target42);
        target[11] = (ImageView)findViewById(R.id.target43);

        G_POW = new ImageView[6];
        G_POW[0] = (ImageView)findViewById(R.id.G_POW1);
        G_POW[1] = (ImageView)findViewById(R.id.G_POW2);
        G_POW[2] = (ImageView)findViewById(R.id.G_POW3);
        G_POW[3] = (ImageView)findViewById(R.id.G_POW4);
        G_POW[4] = (ImageView)findViewById(R.id.G_POW5);
        G_POW[5] = (ImageView)findViewById(R.id.G_POW6);

        R_POW = new ImageView[6];
        R_POW[0] = (ImageView)findViewById(R.id.R_POW1);
        R_POW[1] = (ImageView)findViewById(R.id.R_POW2);
        R_POW[2] = (ImageView)findViewById(R.id.R_POW3);
        R_POW[3] = (ImageView)findViewById(R.id.R_POW4);
        R_POW[4] = (ImageView)findViewById(R.id.R_POW5);
        R_POW[5] = (ImageView)findViewById(R.id.R_POW6);


        value = new BitmapDrawable[11];
        //값이 변하는건 빈칸+ 아군5+적군5
        //다른 건 위로 겹쳐서 가시, 비가시
        Resources res = getResources();
        value[0] = (BitmapDrawable)res.getDrawable(R.drawable.blank);
        value[1] = (BitmapDrawable)res.getDrawable(R.drawable.img21);
        value[2] = (BitmapDrawable)res.getDrawable(R.drawable.img22);
        value[3] = (BitmapDrawable)res.getDrawable(R.drawable.img23);
        value[4] = (BitmapDrawable)res.getDrawable(R.drawable.img24);
        value[5] = (BitmapDrawable)res.getDrawable(R.drawable.img25);
        value[6] = (BitmapDrawable)res.getDrawable(R.drawable.img11);
        value[7] = (BitmapDrawable)res.getDrawable(R.drawable.img12);
        value[8] = (BitmapDrawable)res.getDrawable(R.drawable.img13);
        value[9] = (BitmapDrawable)res.getDrawable(R.drawable.img14);
        value[10] = (BitmapDrawable)res.getDrawable(R.drawable.img15);

        G_POW_LIST= new int[6];
        R_POW_LIST= new int[6];
        for (int i = 0; i < 6; ++i) {
            G_POW_LIST[i] = 0;
            R_POW_LIST[i] = 0;
        }


    }

    private void setPieces() {
        //일단 이미지들의 경로 잡아준다.?
        //인덱스에 따라 12칸의 말의 이미지를 부여해준다.
        for(int i = 0; i< 4; ++i)
            for (int j = 0; j< 3; ++j)
            {
                if (board[i][j] == 0)   //빈칸일경우 빈칸 넣어줌
                    piece[i*3+j].setImageDrawable(value[0]);
                else if (board[i][j] == 1)
                    piece[i*3+j].setImageDrawable(value[1]);
                else if (board[i][j] == 2)
                    piece[i*3+j].setImageDrawable(value[2]);
                else if (board[i][j] == 3)
                    piece[i*3+j].setImageDrawable(value[3]);
                else if (board[i][j] == 4)
                    piece[i*3+j].setImageDrawable(value[4]);
                else if (board[i][j] == 5)
                    piece[i*3+j].setImageDrawable(value[5]);
                else if (board[i][j] == 6)
                    piece[i*3+j].setImageDrawable(value[6]);
                else if (board[i][j] == 7)
                    piece[i*3+j].setImageDrawable(value[7]);
                else if (board[i][j] == 8)
                    piece[i*3+j].setImageDrawable(value[8]);
                else if (board[i][j] == 9)
                    piece[i*3+j].setImageDrawable(value[9]);
                else if (board[i][j] == 10)
                    piece[i*3+j].setImageDrawable(value[10]);

//                for (int k = 1; k <= 5; ++k)
//                    if(board[i][j] == k) iff[i][j] = 1;
            }
        for (int i = 0; i < 6; ++i) {
            G_POW[i].setImageDrawable(value[G_POW_LIST[i]]);
            R_POW[i].setImageDrawable(value[R_POW_LIST[i]]);
        }

    }

    private AlertDialog createDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게임방식");
        builder.setMessage("순서를 정해주세요.");
        //builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("선공", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order = 1;  //상대턴인 상태로 오더링을 부르면 내 턴이 되면서 1턴이 된다.
                //ordering();
            }
        });

        builder.setNegativeButton("후공", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order = 2;  //내턴인채로 오더링을 부르면 상대턴이 되며 1턴이 된다.
                for(int i = 0; i < 4; ++i)
                    for(int j = 0; j < 3; ++j)
                        iff[i][j] = (-1)*iff[i][j];
                //ordering();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }


    public void ordering()
    {
        if(order == 1){ //내턴에서 상대턴이 될때
            order = 2;
            if (GreenVictoryChance == 1) {
                victorydialog = victoryBox();
                victorydialog.show();
            }

            //Toast.makeText(getApplicationContext(),"상대의 차례입니다.", Toast.LENGTH_SHORT).show();
        }
        else if (order == 2) {  //상대턴에서 내턴이 될때
            if (RedVictoryChance == 1) {
                victorydialog = victoryBox();
                victorydialog.show();
            }

            //승리조건이 달성된 상태에서 내 턴이 되면 게임 승리.
            order = 1;
            //Toast.makeText(getApplicationContext(),"플레이어의 차례입니다.", Toast.LENGTH_SHORT).show();
        }
        ++turn; //진행턴수 확인가능
        //Toast.makeText(getApplicationContext(),"turn : "+ turn, Toast.LENGTH_SHORT).show();
    }
    private AlertDialog victoryBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("승리!");
        builder.setMessage((turn/2+turn%2)+"수 승리하셨습니다.");  //턴수를 구하는 방법을 알아보자.
        //builder.setIcon(android.R.drawable.ic_dialog_alert);  //이미지
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("잠깐만", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //지면 뭐할까;
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }










    public void onSelect11(View v)
    {
        if (iff[0][0] == 1) {
            index = 0;
            selectEvent();
        }
    }

    public void onSelect12(View v)
    {
        if (iff[0][1] == 1) {
            index = 1;
            selectEvent();
        }
    }

    public void onSelect13(View v)
    {
        if (iff[0][2] == 1) {
            index = 2;
            selectEvent();
        }
    }

    public void onSelect21(View v)
    {
        if (iff[1][0] == 1) {
            index = 3;
            selectEvent();
        }
    }

    public void onSelect22(View v)
    {
        if (iff[1][1] == 1) {
            index = 4;
            selectEvent();
        }
    }

    public void onSelect23(View v)
    {
        if (iff[1][2] == 1) {
            index = 5;
            selectEvent();
        }
    }

    public void onSelect31(View v)
    {
        if (iff[2][0] == 1) {
            index = 6;
            selectEvent();
        }
    }

    public void onSelect32(View v)
    {
        if (iff[2][1] == 1) {
            index = 7;
            selectEvent();
        }
    }

    public void onSelect33(View v)
    {
        if (iff[2][2] == 1) {
            index = 8;
            selectEvent();
        }
    }

    public void onSelect41(View v)
    {
        if (iff[3][0] == 1) {
            index = 9;
            selectEvent();
        }
    }

    public void onSelect42(View v)
    {
        if (iff[3][1] == 1) {
            index = 10;
            selectEvent();
        }
    }

    public void onSelect43(View v)
    {
        if (iff[3][2] == 1) {
            index = 11;
            selectEvent();
        }
    }

    public void initSelect() {
        if(selectedPiece == 1) {
            for(int i = 0; i< 12; ++i) {
                select[i].setVisibility(View.INVISIBLE);
                target[i].setVisibility(View.INVISIBLE);
            }
            selectedPiece = 0;
        }
        selectedPiece = 1;
    }

    public void selectEvent() {    //선택했을때 노란 줄 표시하는거.
        if (freedom == 1) { //만약 포로를 누르고 말을 누르러 온거면 자유따윈 없다.
            freedom = 0;
            powNum = -1;
        }
        if (iff[index/3][index%3] == 1) {
            initSelect();
            select[index].setVisibility(View.VISIBLE);
            //selectedPiece = 1;    //initselect에 넣음
            setTargetLocation();
            if (board[index/3][index%3] == 1) {
                selectedPieceValue = 1;
                if (iff[(index - 3)/3][(index-3)%3] != 1)
                    target[index - 3].setVisibility(View.VISIBLE);
            }
            if (board[index/3][index%3] == 2) {
                selectedPieceValue = 2;
                targetLocation[2][0] = 0;
                targetLocation[2][2] = 0;
                //다음은 말판에 타겟을 올려줍니다.
                //index를 통해 현재 위치를 파악, targetlocation이 1인 부분에 맞춰 불가시를 가시로 바꿉니다.
                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 3) {
                selectedPieceValue = 3;
                setTargetLocation();
                targetLocation[0][1] = 0;
                targetLocation[1][0] = 0;
                targetLocation[1][2] = 0;
                targetLocation[2][1] = 0;

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 4) {
                selectedPieceValue = 4;
                setTargetLocation();
                targetLocation[0][0] = 0;
                targetLocation[0][2] = 0;
                targetLocation[2][0] = 0;
                targetLocation[2][2] = 0;

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 5) {
                selectedPieceValue = 5;
                setTargetLocation();

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            //여기까지가 빨간 말 타겟선정
            //여기부터는 녹색말 타겟선정
            if (board[index/3][index%3] == 6) {
                selectedPieceValue = 6;
                if (iff[(index + 3)/3][(index+3)%3] != 1)
                    target[index + 3].setVisibility(View.VISIBLE);
            }
            if (board[index/3][index%3] == 7) {
                selectedPieceValue = 7;
                targetLocation[0][0] = 0;
                targetLocation[0][2] = 0;
                //다음은 말판에 타겟을 올려줍니다.
                //index를 통해 현재 위치를 파악, targetlocation이 1인 부분에 맞춰 불가시를 가시로 바꿉니다.
                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 8) {
                selectedPieceValue = 8;
                setTargetLocation();
                targetLocation[0][1] = 0;
                targetLocation[1][0] = 0;
                targetLocation[1][2] = 0;
                targetLocation[2][1] = 0;

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 9) {
                selectedPieceValue = 9;
                setTargetLocation();
                targetLocation[0][0] = 0;
                targetLocation[0][2] = 0;
                targetLocation[2][0] = 0;
                targetLocation[2][2] = 0;

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            if (board[index/3][index%3] == 10) {
                selectedPieceValue = 10;
                setTargetLocation();

                for(int i = 0; i< 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (targetLocation[i][j] == 1 && (index+(3*i+j)-4)<12 && 0<=(index+(3*i+j)-4))
                            if (iff[(index+(3*i+j)-4)/3][(index+(3*i+j)-4)%3] != 1)
                                target[index+(3*i+j)-4].setVisibility(View.VISIBLE);
                    }
                }
            }
            //여기까지
        }
    }
    public void setTargetLocation()
    {
        initTargetLocation();
        int [] tmp;
        tmp = new int[9];

        for (int i = 0 ; i < 9; ++i)
        {
            tmp[i] = index + i - 4;
        }
        for(int i = 0; i< 9; ++i) {
            if ( 0 > tmp[i] && tmp[i] >= 12 )
                targetLocation[i/3][i%3] = 0;

            if (targetLocation[0][1] == 0) {//-3
                targetLocation[0][0] = 0;
                targetLocation[0][2] = 0;
            }
            if (targetLocation[2][1] == 0) {//+3
                targetLocation[2][0] = 0;
                targetLocation[2][2] = 0;
            }
            if (targetLocation[1][0] == 0) {//-1
                targetLocation[0][0] = 0;
                targetLocation[2][0] = 0;
            }
            if (targetLocation[1][2] == 0) {//+1
                targetLocation[0][2] = 0;
                targetLocation[2][2] = 0;
            }
            //이럴때 정상으로 작동, 조건을 반전시키면 문제있는 쪽.
        }   //전후좌우에 벽이 있을 경우 그 행, 열을 없애준다.
        //앞, 현위치, 뒤와 비교했을때 /3 값이 다른 놈을 제거
        //index/3, index/3-1, index/3+1
        if( (index-3)/3 != (index-4)/3 ) targetLocation[0][0] = 0;
        if( (index-3)/3 != (index-2)/3 ) targetLocation[0][2] = 0;
        if( (index)/3 != (index-1)/3) targetLocation[1][0] = 0;
        if( (index)/3 != (index+1)/3) targetLocation[1][2] = 0;
        if( (index+3)/3 != (index+2)/3 ) targetLocation[2][0] = 0;
        if( (index+3)/3 != (index+4)/3 ) targetLocation[2][2] = 0;
        //이부분은 노트를 참고. 앞의 것중 열이 다른걸 찾아서 죽인다.

    }



    public void onTarget11(View v) {
        targetEvent(0);
    }

    public void onTarget12(View v) {
        targetEvent(1);
    }

    public void onTarget13(View v) {
        targetEvent(2);
    }

    public void onTarget21(View v) {
        targetEvent(3);
    }

    public void onTarget22(View v) {
        targetEvent(4);
    }

    public void onTarget23(View v) {
        targetEvent(5);
    }

    public void onTarget31(View v) {
        targetEvent(6);
    }

    public void onTarget32(View v) {
        targetEvent(7);
    }

    public void onTarget33(View v) {
        targetEvent(8);
    }

    public void onTarget41(View v) {
        targetEvent(9);
    }

    public void onTarget42(View v) {
        targetEvent(10);
    }

    public void onTarget43(View v) {
        targetEvent(11);
    }



    public void targetEvent(int num)
    {
        if (order == 1) {
            if (num / 3 == 0 && selectedPieceValue == 1) selectedPieceValue = 2;
            //적 본진에 도달했으므로 후로 변경
            if (num / 3 == 0 && selectedPieceValue == 5) RedVictoryChance = 1;
            //왕이 적 본진에 도달했으므로 승리 조건 열림, 다음턴에 승리.
            if (board[num/3][num%3] == 10) {
                victorydialog = victoryBox();
                victorydialog.show();
            }
            //왕을 잡았으므로 승리.
        }
        if (order == 2) {
            if (num / 3 == 3 && selectedPieceValue == 6) selectedPieceValue = 7;
            //적 본진에 도달했으므로 후로 변경
            if (num / 3 == 3 && selectedPieceValue == 10) GreenVictoryChance = 1;
            //왕이 적 본진에 도달했으므로 승리 조건 열림, 다음턴에 승리.
            if (board[num/3][num%3] == 5) {
                victorydialog = victoryBox();
                victorydialog.show();
            }
            //왕을 잡았으므로 승리.
        }




        if (iff[num/3][num%3] == -1) {   //이동한곳에 적이 있을 경우
            if(order == 1) {
                R_POW_LIST[R_POW_CNT] = (board[num / 3][num % 3]) - 5;    //포로의 피아를 변환시켜
                if(R_POW_LIST[R_POW_CNT] == 2) --R_POW_LIST[R_POW_CNT];
                R_POW[R_POW_CNT].setImageDrawable(value[R_POW_LIST[R_POW_CNT]]); //그놈을 내 철창에 철컹철컹
                ++R_POW_CNT;    //다음 큰집을 준비한다.
            }
            else if(order == 2) {
                G_POW_LIST[G_POW_CNT] = (board[num / 3][num % 3]) + 5;    //포로의 피아를 변환시켜
                if (G_POW_LIST[G_POW_CNT] == 7) --G_POW_LIST[G_POW_CNT];
                G_POW[G_POW_CNT].setImageDrawable(value[G_POW_LIST[G_POW_CNT]]); //그놈을 내 철창에 철컹철컹
                ++G_POW_CNT;    //다음 큰집을 준비한다.
            }
        }
        board[num/3][num%3] = selectedPieceValue;   //그 자리에 내말
        if (freedom == 0)
            board[index/3][index%3] = 0;                //나 있던 자리는 비워
        else if (freedom != 0) {
            //R_POW[R_POW_CNT].setImageDrawable(value[0]);
            if(order == 1) {
                --R_POW_CNT;
                R_POW_LIST[powNum] = 0;
                for (int i = 0; i < R_POW_CNT; ++i) {
                    if (R_POW_LIST[i] == 0) {
                        R_POW_LIST[i] = R_POW_LIST[i + 1];
                        R_POW_LIST[i + 1] = 0;
                    }
                }
//                for (int i = 0; i < 6; ++i)
//                    R_POW[i].setImageDrawable(value[R_POW_LIST[i]]);

            }
            else if (order ==2) {
                --G_POW_CNT;
                G_POW_LIST[powNum] = 0;
                for (int i = 0; i < G_POW_CNT; ++i) {
                    if (G_POW_LIST[i] == 0) {
                        G_POW_LIST[i] = G_POW_LIST[i + 1];
                        G_POW_LIST[i + 1] = 0;
                    }
                }
//                for (int i = 0; i < 6; ++i)
//                    G_POW[i].setImageDrawable(value[G_POW_LIST[i]]);

            }
            freedom = 0;
            powNum = -1;
        }
        for (int i = 0; i < 12 ; ++i)
        {
            target[i].setVisibility(View.INVISIBLE);
            select[i].setVisibility(View.INVISIBLE);
        }
        //if (board[0][0] == 1 && board[0][1] == 1 && board[0][2] == 1 )
        //    board[num/3][num%3] = 2;
        setPieces();    //말 다시 뿌리고
        setIff();       //피아식별 다시하고

        if(order == 1) {
            for(int i = 0; i < 4; ++i)
                for(int j = 0; j < 3; ++j)
                    iff[i][j] = (-1)*iff[i][j];
        }

        ordering();     //턴넘기고
        selectedPiece = 0;  //선택한 말이 없다로 다시 변경해줌.
        selectedPieceValue = 0; //선택된 말의 값이 빈칸.
        index = -1;           //인덱스를 해당사항없음으로.
        //selectedPieceValue; //건드리는 내 말이 어떤 말인지. 자인지 상인지. 내 인덱스
        //셋 피시스가 하는 일이 있다.
        //그놈은 보드에 깔린 인덱스 값을 보고 값을 넣어준다.
        /////////////////////////////////////////
        //상대자리의 인덱스를 POW로 옮긴다.상대의 인덱스는 현위치board[][]에 들어있다.
        //상대자리엔 내 인덱스를 넣는다.
        ////////////////////////////////////////////
        //그자리가 적이 아니면. 빈값대신 내 인덱스를 넣는다.
        ///////////////////////////////////////
        //내자리는 빈칸으로.
        //타겟을 인비지블로 변경.
        //setPieces를 때려준다.
        //상대턴으로 바꾼다.
        ////////////////////////////////
    }

    //포로 처분
    public void onR_POW1(View v) {
        if(order ==1)
            powEvent(0);

    }
    public void onR_POW2(View v) {
        if(order ==1)
            powEvent(1);
    }
    public void onR_POW3(View v) {
        if(order ==1)
            powEvent(2);
    }
    public void onR_POW4(View v) {
        if(order ==1)
            powEvent(3);
    }
    public void onR_POW5(View v) {
        if(order ==1)
            powEvent(4);
    }
    public void onR_POW6(View v) {
        if(order ==1)
            powEvent(5);
    }


    public void onG_POW1(View v) {
        if(order == 2)
            powEvent(0);

    }
    public void onG_POW2(View v) {
        if(order == 2)
            powEvent(1);
    }
    public void onG_POW3(View v) {
        if(order == 2)
            powEvent(2);
    }
    public void onG_POW4(View v) {
        if(order == 2)
            powEvent(3);
    }
    public void onG_POW5(View v) {
        if(order == 2)
            powEvent(4);
    }
    public void onG_POW6(View v) {
        if(order == 2)
            powEvent(5);
    }


    public void powEvent(int num) {
        initSelect();
        if(R_POW_LIST[num] != 0 && order == 1)  //해당칸에 포로가 있을때! (0)
            selectedPieceValue = R_POW_LIST[num]; //선택된 값에 포로넣어
        if(G_POW_LIST[num] != 0 && order == 2)  //해당칸에 포로가 있을때! (0)
            selectedPieceValue = G_POW_LIST[num]; //선택된 값에 포로넣어

        freedom = 1;    //자유로울 놈이 한놈 있다. 이건 타겟이벤트에서 없어짐
        powNum = num;   //어떤 놈을 고기방패로 쓸것인가.
        for (int i = 1; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (iff[i][j] == 0)
                    target[3 * i + j].setVisibility(View.VISIBLE);  //아무도 없는 곳에 떨궈줄 수 있다.
            }
        }//타겟이 나타났으므로 타겟 이벤트가 동작한다. 찍은 곳의 인덱스를 이용한다.
    }

///////////////상대턴을 해봅시다.






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
