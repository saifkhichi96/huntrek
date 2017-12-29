function createCubeVertices(color) {
    var cube = new Object();
    cube.vertexPositionData = [
        -0.5, 0.5, 0.5,   // 0
        0.5, 0.5, 0.5,   // 1
        -0.5, -0.5, 0.5,  // 2
        0.5, -0.5, 0.5,  // 3

        -0.5, 0.5, -0.5,  // 4
        0.5, 0.5, -0.5,  // 5
        -0.5, -0.5, -0.5, // 6
        0.5, -0.5, -0.5  // 7
    ];
    if (color == null) {
        cube.vertexColorData = [
            1.0, 0.0, 0.0,   // 0
            0.0, 1.0, 0.0,   // 1
            0.0, 0.0, 1.0,   // 2
            1.0, 1.0, 0.0,   // 3

            1.0, 0.0, 0.0,   // 4
            0.0, 1.0, 0.0,   // 5
            0.0, 0.0, 1.0,   // 6
            1.0, 1.0, 0.0,   // 7
        ];
    } else {
        cube.vertexColorData = [
            color.r, color.g, color.b,   // 0
            color.r, color.g, color.b,   // 1
            color.r, color.g, color.b,   // 2
            color.r, color.g, color.b,   // 3

            color.r, color.g, color.b,   // 0
            color.r, color.g, color.b,   // 1
            color.r, color.g, color.b,   // 2
            color.r, color.g, color.b,   // 3
        ];
    }
    cube.normalData = [
        0.57, 0.57, -0.57,
        0.57, -0.57, -0.57,
        -0.57, -0.57, -0.57,
        -0.57, 0.57, -0.57,
        0.57, 0.57, 0.57,
        0.57, -0.57, 0.57,
        -0.57, -0.57, 0.57,
        -0.57, 0.57, 0.57
    ];
    cube.indexData = [
        0, 2, 3,
        0, 3, 1,

        0, 4, 5,
        0, 5, 1,

        0, 4, 6,
        0, 6, 2,

        2, 6, 7,
        2, 7, 3,

        1, 5, 7,
        1, 7, 3,

        4, 6, 7,
        4, 7, 5
    ]
    return cube;
}

function Cube(gl, color = null) {
    this.position = [0, 0, 0];
    this.rotation = 0;
    this.scaleFactor = [1, 1, 1];

    var vertices = createCubeVertices(color);

    this.vertexNormalBuffer = gl.createBuffer();
    this.vertexNormalBuffer.itemSize = 3;
    this.vertexNormalBuffer.numItems = vertices.normalData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexNormalBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.normalData), gl.STATIC_DRAW);

    this.vertexPositionBuffer = gl.createBuffer();
    this.vertexPositionBuffer.itemSize = 3;
    this.vertexPositionBuffer.numItems = vertices.vertexPositionData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexPositionBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.vertexPositionData), gl.STATIC_DRAW);

    this.vertexColorBuffer = gl.createBuffer();
    this.vertexColorBuffer.itemSize = 3;
    this.vertexColorBuffer.numItems = vertices.vertexColorData.length / 3;
    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexColorBuffer);
    gl.bufferData(gl.ARRAY_BUFFER, flatten(vertices.vertexColorData), gl.STATIC_DRAW);

    this.vertexIndexBuffer = gl.createBuffer();
    this.vertexIndexBuffer.numItems = vertices.indexData.length;
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.vertexIndexBuffer);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(vertices.indexData), gl.STATIC_DRAW);
}

Cube.prototype.setPosition = function (position) {
    this.position = position;
}

Cube.prototype.setRotation = function (r) {
    this.rotation += r;
    if (this.rotation >= 360) this.rotation = 0;
}

Cube.prototype.scale = function (S) {
    this.scaleFactor = S;
}

Cube.prototype.render = function (gl, shaderProgram, modelMatrix, viewMatrix) {
    mat4.identity(modelMatrix);
    mat4.multiply(modelMatrix, viewMatrix);

    mat4.translate(modelMatrix, this.position);
    mat4.scale(modelMatrix, this.scaleFactor);
    mat4.rotate(modelMatrix, this.rotation, [0, 1, 0]);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexPositionBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.vertexPositionBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexColorBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexColorAttribute, this.vertexColorBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ARRAY_BUFFER, this.vertexNormalBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexNormalAttribute, this.vertexNormalBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.vertexIndexBuffer);
    setMatrixUniforms();

    gl.drawElements(gl.TRIANGLES, this.vertexIndexBuffer.numItems, gl.UNSIGNED_SHORT, 0);
}