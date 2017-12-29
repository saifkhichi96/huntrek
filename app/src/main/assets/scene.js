var gl;
var shaderProgram;

var projectionMatrix = mat4.create();   // Projection Matrix
var modelMatrix = mat4.create();        // Model Matrix
var viewMatrix = mat4.create();         // View Matrix

var x = 0.0;
var y = 0.0;
var z = 10.0;

var rv = 0;
var rh = 0;

var mGround;
var mDirectionalLight;

var mTreasureChest;
var mTreasureGuard;

var fieldOfView = 45;   // Camera's field of view (in degrees)
var zNear = 0.1;
var zFar = 100.0;
var motionSpeed = 0.50;
var rotationSpeed = 0.50;

function init() {
    var canvas = document.getElementById("webgl");
    try {
        gl = canvas.getContext("experimental-webgl");
        gl.viewportWidth = canvas.width;
        gl.viewportHeight = canvas.height;
    } catch (e) {

    }
    if (gl) {
        // Initialize shaders
        initShaderProgram();

        // Add lighting to the scene
        mDirectionalLight = new DirectionalLight(
            shaderProgram.lightingDirectionUniform,
            shaderProgram.lightingColorUniform
        );
        mDirectionalLight.setColor(0.494, 0.416, 0.125);
        mDirectionalLight.setDirection(-1.0, -1.0, -1.0);

        mGround = new Cube(gl);
        mGround.setPosition([0, -10, 0]);
        mGround.scale([1000, 0.1, 1000]);

        mTreasureChest = new Cube(gl);
        mTreasureChest.setPosition([0, -5, -70]);
        mTreasureChest.scale([10, 10, 10]);

        mTreasureGuard = new Sphere(gl, 2, [0, 0.2, 0.7]);
        mTreasureGuard.setPosition([0, -8, -50]);

        // Render scene
        gl.clearColor(0.012, 0.026, 0.74, 1.0);
        gl.enable(gl.DEPTH_TEST);
        render();
    }
}

function initShaderProgram() {
    shaderProgram = initShaders(gl, "shader-vs", "shader-fs");
    gl.useProgram(shaderProgram);
    shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "vertexPosition");
    gl.enableVertexAttribArray(shaderProgram.vertexPositionAttribute);

    shaderProgram.vertexNormalAttribute = gl.getAttribLocation(shaderProgram, "vertexNormal");
    gl.enableVertexAttribArray(shaderProgram.vertexNormalAttribute);

    shaderProgram.vertexColorAttribute = gl.getAttribLocation(shaderProgram, "vertexColor");
    gl.enableVertexAttribArray(shaderProgram.vertexColorAttribute);

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "projectionMatrix");
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "modelViewMatrix");
    shaderProgram.nMatrixUniform = gl.getUniformLocation(shaderProgram, "normalMatrix");

    shaderProgram.useLightingUniform = gl.getUniformLocation(shaderProgram, "useLighting");

    shaderProgram.lightingDirectionUniform = gl.getUniformLocation(shaderProgram, "lightDirection");
    shaderProgram.lightingColorUniform = gl.getUniformLocation(shaderProgram, "lightColor");
}

function setMatrixUniforms() {
    // Set projection matrix
    gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, projectionMatrix);

    // Set model-view matrix
    gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, modelMatrix);

    // Calculate and set normal matrix
    var normalMatrix = mat3.create();
    mat4.toInverseMat3(modelMatrix, normalMatrix);
    mat3.transpose(normalMatrix);
    gl.uniformMatrix3fv(shaderProgram.nMatrixUniform, false, normalMatrix);
}

function render() {
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

    mat4.perspective(fieldOfView, gl.viewportWidth / gl.viewportHeight, zNear, zFar, projectionMatrix);

    // Reset camera
    mat4.identity(viewMatrix);

    // Look up/down
    mat4.rotate(viewMatrix, degToRad(rv), [1, 0, 0]);

    // Look around horizontally
    mat4.translate(viewMatrix, [x, y, z]);
    mat4.rotate(viewMatrix, degToRad(rh), [0, 1, 0]);
    mat4.translate(viewMatrix, [-x, -y, -z]);

    // Move around
    mat4.translate(viewMatrix, [x, y, z]);

    // Update lighting
    mDirectionalLight.render(gl);

    // Render current frame
    mGround.render(gl, shaderProgram, modelMatrix, viewMatrix);

    mTreasureChest.setRotation(0.005);
    mTreasureChest.render(gl, shaderProgram, modelMatrix, viewMatrix);

    mTreasureGuard.rotateAround(1, [0, -5, -70]);
    mTreasureGuard.render(gl, shaderProgram, modelMatrix, viewMatrix);

    // Request new frame
    requestAnimFrame(render);
}

function degToRad(degrees) {
    return degrees * Math.PI / 180;
}

function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}